<script setup lang="ts">
definePageMeta({
  layout: 'default',
  middleware: 'seller-guard'
})

type ListingImage = {
  file: File
  previewUrl: string
  isUploading: boolean
}

const config = useRuntimeConfig()
const toast = useToast()

const isSubmitting = ref(false)
const fileInput = ref<HTMLInputElement | null>(null)

const form = reactive({
  title: '',
  location: '',
  rating: 5,
  pricePerNight: 0,
  description: '',
  amenities: ['']
})

const images = ref<ListingImage[]>([])

const previewImages = computed(() => images.value.map(image => image.previewUrl))

function openFilePicker() {
  fileInput.value?.click()
}

function onImagesSelected(event: Event) {
  const target = event.target as HTMLInputElement
  const files = Array.from(target.files || [])

  files.forEach((file) => {
    images.value.push({
      file,
      previewUrl: URL.createObjectURL(file),
      isUploading: false
    })
  })

  target.value = ''
}

function removeImage(index: number) {
  images.value.splice(index, 1)
}

function addAmenity() {
  form.amenities.push('')
}

function removeAmenity(index: number) {
  form.amenities.splice(index, 1)
}

async function uploadImage(image: ListingImage) {
  image.isUploading = true

  try {
    const formData = new FormData()
    formData.append('file', image.file)

    const response = await $fetch<{ imageUrl: string }>(
      `${config.public.apiBase}/images/upload`,
      {
        method: 'POST',
        credentials: 'include',
        body: formData
      }
    )

    return response.imageUrl
  } finally {
    image.isUploading = false
  }
}

async function createListing() {
  isSubmitting.value = true

  try {
    const uploadedImageUrls = await Promise.all(
      images.value.map(image => uploadImage(image))
    )

    await $fetch(`${config.public.apiBase}/seller/listings`, {
      method: 'POST',
      credentials: 'include',
      body: {
        title: form.title,
        location: form.location,
        description: form.description,
        pricePerNight: form.pricePerNight,
        rating: form.rating,
        images: uploadedImageUrls,
        amenities: form.amenities.filter(amenity => amenity.trim() !== '')
      }
    })

    toast.add({
      title: 'Listing created',
      description: 'Your listing was submitted for approval.',
      color: 'success'
    })

    await navigateTo('/seller')
  } catch (error) {
    console.error(error)

    toast.add({
      title: 'Error',
      description: 'Failed to create listing.',
      color: 'error'
    })
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-white text-slate-900">
    <UContainer class="py-12">
      <header class="mb-8">
        <UInput
          v-model="form.title"
          placeholder="Property title"
          size="xl"
          class="mb-4"
        />

        <div class="flex flex-col gap-4 md:flex-row md:items-center">
          <UInput
            v-model="form.location"
            placeholder="Property location"
            icon="i-heroicons-map-pin"
            class="w-full md:max-w-xl"
          />

          <UInput
            v-model.number="form.rating"
            type="number"
            min="1"
            max="5"
            placeholder="Rating"
            icon="i-heroicons-star-solid"
            class="w-full md:max-w-32"
          />
        </div>
      </header>

      <div class="mb-12">
        <UCarousel
          v-if="previewImages.length"
          v-slot="{ item }"
          :items="previewImages"
          :ui="{ item: 'basis-full' }"
          :prev="{ color: 'primary' }"
          :next="{ color: 'primary' }"
          class="overflow-hidden rounded-2xl shadow-lg"
          arrows
          indicators
        >
          <img :src="item" class="h-96 w-full object-cover" draggable="false" />
        </UCarousel>

        <UCard
          v-else
          class="flex h-96 items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-slate-50"
        >
          <div class="text-center text-slate-500">
            <UIcon name="i-lucide-image-plus" class="mx-auto mb-3 size-10" />
            <p class="font-medium">Upload photos to preview your listing.</p>
          </div>
        </UCard>
      </div>

      <div class="grid grid-cols-1 gap-12 lg:grid-cols-3">
        <div class="lg:col-span-2">
          <h2 class="mb-4 border-b border-slate-200 pb-2 text-2xl font-bold">
            About the Property
          </h2>

          <UTextarea
            v-model="form.description"
            :rows="8"
            placeholder="Describe your property..."
            class="mb-8 w-full"
          />

          <h3 class="mb-4 text-xl font-bold">Images</h3>

          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            multiple
            class="hidden"
            @change="onImagesSelected"
          />

          <div class="mb-8 space-y-4">
            <UButton
              label="Upload Images"
              icon="i-lucide-upload"
              variant="soft"
              color="neutral"
              @click="openFilePicker"
            />

            <div
              v-if="images.length"
              class="grid grid-cols-2 gap-4 md:grid-cols-3"
            >
              <div
                v-for="(image, index) in images"
                :key="image.previewUrl"
                class="relative overflow-hidden rounded-xl border bg-slate-50"
              >
                <img
                  :src="image.previewUrl"
                  class="h-32 w-full object-cover"
                />

                <div
                  v-if="image.isUploading"
                  class="absolute inset-0 flex items-center justify-center bg-black/40"
                >
                  <UIcon
                    name="i-lucide-loader-circle"
                    class="h-7 w-7 animate-spin text-white"
                  />
                </div>

                <UButton
                  icon="i-lucide-trash"
                  color="error"
                  variant="solid"
                  size="xs"
                  class="absolute right-2 top-2"
                  :disabled="isSubmitting"
                  @click="removeImage(index)"
                />
              </div>
            </div>
          </div>

          <h3 class="mb-4 text-xl font-bold">Amenities</h3>

          <div class="space-y-3">
            <div
              v-for="(amenity, index) in form.amenities"
              :key="index"
              class="flex gap-2"
            >
              <UInput
                v-model="form.amenities[index]"
                placeholder="Amenity"
                icon="i-heroicons-check-circle"
                class="flex-1"
              />

              <UButton
                icon="i-lucide-trash"
                color="error"
                variant="soft"
                :disabled="form.amenities.length === 1"
                @click="removeAmenity(index)"
              />
            </div>

            <UButton
              label="Add Amenity"
              icon="i-lucide-plus"
              variant="soft"
              color="neutral"
              @click="addAmenity"
            />
          </div>
        </div>

        <div>
          <UCard class="sticky top-6 bg-white shadow-lg ring-1 ring-slate-200">
            <div class="space-y-4">
              <div>
                <label class="mb-2 block text-sm font-medium text-slate-700">
                  Price per night
                </label>

                <UInput
                  v-model.number="form.pricePerNight"
                  type="number"
                  min="0"
                  placeholder="0"
                  icon="i-lucide-euro"
                  size="xl"
                />
              </div>

              <UButton
                label="Create Listing"
                icon="i-lucide-plus"
                size="xl"
                block
                class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
                :loading="isSubmitting"
                :disabled="isSubmitting"
                @click="createListing"
              />
            </div>
          </UCard>
        </div>
      </div>
    </UContainer>
  </div>
</template>