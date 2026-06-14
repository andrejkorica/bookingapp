<script setup lang="ts">
definePageMeta({
  layout: "default",
  middleware: "seller-guard",
});

const form = reactive({
  title: "",
  location: "",
  rating: 5,
  pricePerNight: 0,
  images: [""],
  description: "",
  amenities: [""],
});

const previewImages = computed(() =>
  form.images.filter((image) => image.trim() !== ""),
);

function addImage() {
  form.images.push("");
}

function removeImage(index: number) {
  form.images.splice(index, 1);
}

function addAmenity() {
  form.amenities.push("");
}

function removeAmenity(index: number) {
  form.amenities.splice(index, 1);
}

const config = useRuntimeConfig();
const toast = useToast();
const isSubmitting = ref(false);

async function createListing() {
  isSubmitting.value = true;

  try {
    await $fetch(`${config.public.apiBase}/seller/listings`, {
      method: "POST",
      credentials: "include",
      body: {
        title: form.title,
        location: form.location,
        description: form.description,
        pricePerNight: form.pricePerNight,
        rating: form.rating,
        images: form.images.filter((image) => image.trim() !== ""),
        amenities: form.amenities.filter((amenity) => amenity.trim() !== ""),
      },
    });

    toast.add({
      title: "Listing created",
      description: "Your listing was submitted for approval.",
      color: "success",
    });

    await navigateTo("/seller");
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to create listing.",
      color: "error",
    });
  } finally {
    isSubmitting.value = false;
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
          class="mb-4" />

        <div class="flex flex-col gap-4 md:flex-row md:items-center">
          <UInput
            v-model="form.location"
            placeholder="Property location"
            icon="i-heroicons-map-pin"
            class="w-full md:max-w-xl" />

          <UInput
            v-model.number="form.rating"
            type="number"
            min="1"
            max="5"
            placeholder="Rating"
            icon="i-heroicons-star-solid"
            class="w-full md:max-w-32" />
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
          indicators>
          <img :src="item" class="h-96 w-full object-cover" draggable="false" />
        </UCarousel>

        <UCard
          v-else
          class="flex h-96 items-center justify-center rounded-2xl border border-dashed border-slate-300 bg-slate-50">
          <div class="text-center text-slate-500">
            <UIcon name="i-lucide-image-plus" class="mx-auto mb-3 size-10" />

            <p class="font-medium">
              Add image URLs below to preview your listing.
            </p>
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
            class="mb-8 w-full" />

          <h3 class="mb-4 text-xl font-bold">Images</h3>

          <div class="mb-8 space-y-3">
            <div
              v-for="(image, index) in form.images"
              :key="index"
              class="flex gap-2">
              <UInput
                v-model="form.images[index]"
                placeholder="Image URL"
                icon="i-lucide-image"
                class="flex-1" />

              <UButton
                icon="i-lucide-trash"
                color="error"
                variant="soft"
                :disabled="form.images.length === 1"
                @click="removeImage(index)" />
            </div>

            <UButton
              label="Add Image"
              icon="i-lucide-plus"
              variant="soft"
              color="neutral"
              @click="addImage" />
          </div>

          <h3 class="mb-4 text-xl font-bold">Amenities</h3>

          <div class="space-y-3">
            <div
              v-for="(amenity, index) in form.amenities"
              :key="index"
              class="flex gap-2">
              <UInput
                v-model="form.amenities[index]"
                placeholder="Amenity"
                icon="i-heroicons-check-circle"
                class="flex-1" />

              <UButton
                icon="i-lucide-trash"
                color="error"
                variant="soft"
                :disabled="form.amenities.length === 1"
                @click="removeAmenity(index)" />
            </div>

            <UButton
              label="Add Amenity"
              icon="i-lucide-plus"
              variant="soft"
              color="neutral"
              @click="addAmenity" />
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
                  size="xl" />
              </div>

              <UButton
                label="Create Listing"
                icon="i-lucide-plus"
                size="xl"
                block
                class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
                :loading="isSubmitting"
                :disabled="isSubmitting"
                @click="createListing" />
            </div>
          </UCard>
        </div>
      </div>
    </UContainer>
  </div>
</template>
