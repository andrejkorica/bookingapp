<script setup lang="ts">
import type { DateValue } from "@internationalized/date";
import { today, getLocalTimeZone } from "@internationalized/date";
import CreateListingUnits from "../../../components/listings/create/CreateListingUnits.vue";
import CreateListingAvailability from "../../../components/listings/create/CreateListingAvailability.vue";
import type { PriceAdjustment } from "~/types/ListingTypes.js";
import CreateListingLocation from "../../../components/listings/create/CreateListingLocation.vue";
import CreateListingImagePreview from "../../../components/listings/create/CreateListingImagePreview.vue";
import CreateListingPriceAdjustments from "~/components/listings/create/CreateListingPriceAdjustments.vue";
import { unitTypes } from "~/constants/UnitConstants.js";
import type { ListingUnit } from "~/types/ListingTypes.js";
import type { ListingImage } from "~/types/ListingTypes.js";

definePageMeta({
  middleware: "seller-guard",
});

const config = useRuntimeConfig();
const toast = useToast();
const router = useRouter();

const isSubmitting = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const form = reactive({
  title: "",
  location: "",
  latitude: null as number | null,
  longitude: null as number | null,
  rating: 5,
  description: "",
  amenities: [""],
});

const images = ref<ListingImage[]>([]);

const listingUnits = ref<ListingUnit[]>(
  unitTypes.map((unit) => ({
    type: unit.value,
    label: unit.label,
    quantity: 0,
    maxGuests: unit.maxGuests,
    roomCount: unit.roomCount,
    pricePerNight: 0,
  })),
);

const availableFrom = shallowRef<DateValue>(today(getLocalTimeZone()));

const priceAdjustments = ref<PriceAdjustment[]>([
  {
    startDate: "",
    endDate: "",
    percent: 0,
  },
]);

const selectedUnits = computed(() =>
  listingUnits.value.filter(
    (unit) => Number(unit.quantity) > 0 && Number(unit.pricePerNight) > 0,
  ),
);

const lowestPrice = computed(() => {
  if (!selectedUnits.value.length) {
    return 0;
  }

  return Math.min(
    ...selectedUnits.value.map((unit) => Number(unit.pricePerNight)),
  );
});

const highestPrice = computed(() => {
  if (!selectedUnits.value.length) {
    return 0;
  }

  return Math.max(
    ...selectedUnits.value.map((unit) => Number(unit.pricePerNight)),
  );
});

function openFilePicker() {
  fileInput.value?.click();
}

function onImagesSelected(event: Event) {
  const target = event.target as HTMLInputElement;
  const files = Array.from(target.files || []);

  files.forEach((file) => {
    images.value.push({
      file,
      previewUrl: URL.createObjectURL(file),
      isUploading: false,
    });
  });

  target.value = "";
}

function removeImage(index: number) {
  const image = images.value[index];

  if (image) {
    URL.revokeObjectURL(image.previewUrl);
  }

  images.value.splice(index, 1);
}

function addAmenity() {
  form.amenities.push("");
}

function removeAmenity(index: number) {
  form.amenities.splice(index, 1);
}

async function uploadImage(image: ListingImage) {
  if (!image.file) {
    throw new Error("Image file is missing.");
  }

  image.isUploading = true;

  try {
    const formData = new FormData();
    formData.append("file", image.file);

    const response = await $fetch<{ imageUrl: string }>(
      `${config.public.apiBase}/images/upload`,
      {
        method: "POST",
        credentials: "include",
        body: formData,
      },
    );

    return response.imageUrl;
  } finally {
    image.isUploading = false;
  }
}
async function createListing() {
  isSubmitting.value = true;

  try {
    const uploadedImageUrls = await Promise.all(
      images.value.map((image) => uploadImage(image)),
    );

    await $fetch(`${config.public.apiBase}/seller/listings`, {
      method: "POST",
      credentials: "include",
      body: {
        title: form.title,
        location: form.location,
        latitude: form.latitude,
        longitude: form.longitude,
        description: form.description,
        rating: form.rating,
        images: uploadedImageUrls,
        amenities: form.amenities.filter((amenity) => amenity.trim() !== ""),
        availableFrom: availableFrom.value.toString(),
        lowestPrice: lowestPrice.value,
        highestPrice: highestPrice.value,
        units: selectedUnits.value.map((unit) => ({
          type: unit.type,
          label: unit.label,
          quantity: Number(unit.quantity),
          roomCount: Number(unit.roomCount),
          maxGuests: Number(unit.maxGuests),
          pricePerNight: Number(unit.pricePerNight),
        })),
        priceAdjustments: priceAdjustments.value
          .filter(
            (adjustment) =>
              adjustment.startDate &&
              adjustment.endDate &&
              Number(adjustment.percent) !== 0,
          )
          .map((adjustment) => ({
            startDate: adjustment.startDate,
            endDate: adjustment.endDate,
            percent: Number(adjustment.percent),
          })),
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

onUnmounted(() => {
  images.value.forEach((image) => URL.revokeObjectURL(image.previewUrl));
});
</script>

<template>
  <div class="min-h-screen bg-white text-slate-900">
    <UContainer class="py-12">
      <header class="mb-8">
        <h1
          class="mb-6 text-4xl font-bold tracking-tight text-slate-900 md:text-5xl"
        >
          Create Listing
        </h1>

        <div class="mb-6 flex items-center justify-between gap-4">
          <div
            class="flex min-w-0 flex-1 flex-col gap-4 md:flex-row md:items-center"
          >
            <UInput
              v-model="form.title"
              placeholder="Property title"
              size="xl"
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

          <UButton
            label="Back"
            icon="i-lucide-arrow-left"
            variant="soft"
            color="neutral"
            class="shrink-0"
            @click="router.back()"
          />
        </div>
      </header>

      <div class="mb-12">
        <CreateListingImagePreview :images="images" />
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

          <CreateListingLocation
            v-model:location="form.location"
            v-model:latitude="form.latitude"
            v-model:longitude="form.longitude"
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
                <img :src="image.previewUrl" class="h-32 w-full object-cover" />

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

          <div class="my-10 border-t border-slate-200" />

          <CreateListingUnits v-model="listingUnits" />

          <CreateListingAvailability v-model="availableFrom" />

          <div class="my-10 border-t border-slate-200" />

          <CreateListingPriceAdjustments v-model="priceAdjustments" />
        </div>

        <div>
          <UCard class="sticky top-6 bg-white shadow-lg ring-1 ring-slate-200">
            <div class="space-y-4">
              <div>
                <p class="mb-2 text-sm font-medium text-slate-700">
                  Price range
                </p>

                <p
                  v-if="selectedUnits.length"
                  class="text-3xl font-bold text-slate-900"
                >
                  €{{ lowestPrice }} - €{{ highestPrice }}
                </p>

                <p v-else class="text-sm text-slate-500">
                  Add at least one unit with quantity and price.
                </p>
              </div>

              <UButton
                label="Create Listing"
                icon="i-lucide-plus"
                size="xl"
                block
                class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
                :loading="isSubmitting"
                :disabled="isSubmitting || selectedUnits.length === 0"
                @click="createListing"
              />
            </div>
          </UCard>
        </div>
      </div>
    </UContainer>
  </div>
</template>
