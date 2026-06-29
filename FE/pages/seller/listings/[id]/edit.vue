<script setup lang="ts">
import type { DateValue } from "@internationalized/date";
import { parseDate, today, getLocalTimeZone } from "@internationalized/date";
import { unitTypes } from "~/constants/UnitConstants.js";
import type {
  Listing,
  ListingUnit,
  PriceAdjustment,
} from "~/types/ListingTypes.js";
import CreateListingAvailability from "~/components/listings/create/CreateListingAvailability.vue";
import CreateListingPriceAdjustments from "~/components/listings/create/CreateListingPriceAdjustments.vue";
import CreateListingLocation from "~/components/listings/create/CreateListingLocation.vue";
import CreateListingImagePreview from "~/components/listings/create/CreateListingImagePreview.vue";
import CreateListingUnits from "~/components/listings/create/CreateListingUnits.vue";

definePageMeta({
  layout: "default",
  middleware: "seller-guard",
});

type ListingImage = {
  file?: File;
  previewUrl: string;
  isUploading: boolean;
  existingUrl?: string;
};

const route = useRoute();
const config = useRuntimeConfig();
const toast = useToast();

const isLoading = ref(false);
const isSubmitting = ref(false);
const fileInput = ref<HTMLInputElement | null>(null);

const form = reactive({
  title: "",
  location: "",
  rating: 5,
  description: "",
  amenities: [""],
  latitude: null as number | null,
  longitude: null as number | null,
});

const images = ref<ListingImage[]>([]);

const listingUnits = ref<ListingUnit[]>(
  unitTypes.map((unitType) => ({
    type: unitType.value,
    label: unitType.label,
    quantity: 0,
    maxGuests: unitType.maxGuests,
    roomCount: unitType.roomCount,
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

const previewImages = computed(() =>
  images.value.map((image) => ({
    previewUrl: image.previewUrl,
  })),
);

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

  if (image?.file) {
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

async function fetchListing() {
  isLoading.value = true;

  try {
    const listing = await $fetch<Listing>(
      `${config.public.apiBase}/listings/${route.params.id}`,
      {
        credentials: "include",
      },
    );

    form.title = listing.title;
    form.location = listing.location;
    form.rating = listing.rating;
    form.description = listing.description;
    form.amenities = listing.amenities?.length ? [...listing.amenities] : [""];
    form.latitude = listing.latitude;
    form.longitude = listing.longitude;

    availableFrom.value = listing.availableFrom
      ? parseDate(listing.availableFrom)
      : today(getLocalTimeZone());

    priceAdjustments.value = listing.priceAdjustments?.length
      ? listing.priceAdjustments.map((adjustment) => ({
          startDate: adjustment.startDate,
          endDate: adjustment.endDate,
          percent: Number(adjustment.percent),
        }))
      : [
          {
            startDate: "",
            endDate: "",
            percent: 0,
          },
        ];

    const savedUnits = listing.units || [];

    listingUnits.value = unitTypes.map((unitType) => {
      const savedUnit = savedUnits.find(
        (unit: ListingUnit) => unit.type === unitType.value,
      );

      return {
        type: unitType.value,
        label: unitType.label,
        quantity: savedUnit ? Number(savedUnit.quantity) : 0,
        maxGuests: unitType.maxGuests,
        roomCount: unitType.roomCount,
        pricePerNight: savedUnit ? Number(savedUnit.pricePerNight) : 0,
      };
    });

    images.value =
      listing.images?.map((url) => ({
        previewUrl: url,
        existingUrl: url,
        isUploading: false,
      })) || [];
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to load listing.",
      color: "error",
    });
  } finally {
    isLoading.value = false;
  }
}

async function uploadImage(image: ListingImage) {
  if (image.existingUrl) {
    return image.existingUrl;
  }

  if (!image.file) {
    return null;
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

async function updateListing() {
  isSubmitting.value = true;

  try {
    const imageUrls = await Promise.all(
      images.value.map((image) => uploadImage(image)),
    );

    await $fetch(
      `${config.public.apiBase}/seller/listings/${route.params.id}/update`,
      {
        method: "POST",
        credentials: "include",
        body: {
          title: form.title,
          location: form.location,
          description: form.description,
          rating: form.rating,
          lowestPrice: lowestPrice.value,
          highestPrice: highestPrice.value,
          images: imageUrls.filter((url): url is string => !!url),
          amenities: form.amenities.filter((amenity) => amenity.trim() !== ""),
          availableFrom: availableFrom.value.toString(),
          units: selectedUnits.value.map((unit) => ({
            type: unit.type,
            label: unit.label,
            quantity: Number(unit.quantity),
            maxGuests: Number(unit.maxGuests),
            roomCount: Number(unit.roomCount),
            pricePerNight: Number(unit.pricePerNight),
          })),
          latitude: form.latitude,
          longitude: form.longitude,
          priceAdjustments: priceAdjustments.value
            .filter(
              (adjustment) =>
                adjustment.startDate &&
                adjustment.endDate &&
                Number(adjustment.percent) > 0,
            )
            .map((adjustment) => ({
              startDate: adjustment.startDate,
              endDate: adjustment.endDate,
              percent: Number(adjustment.percent),
            })),
        },
      },
    );

    toast.add({
      title: "Listing updated",
      description: "Your listing was updated successfully.",
      color: "success",
    });

    await navigateTo("/seller/listings/manage");
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to update listing.",
      color: "error",
    });
  } finally {
    isSubmitting.value = false;
  }
}

onMounted(fetchListing);

onUnmounted(() => {
  images.value.forEach((image) => {
    if (image.file) {
      URL.revokeObjectURL(image.previewUrl);
    }
  });
});
</script>

<template>
  <div class="min-h-screen bg-white text-slate-900">
    <UContainer class="py-12">
      <div v-if="isLoading" class="py-16 text-center text-slate-500">
        Loading listing...
      </div>

      <div v-else>
        <header class="mb-8">
          <div class="mb-6 flex items-center justify-between">
            <div>
              <h1 class="text-3xl font-bold text-slate-900">Edit Listing</h1>

              <p class="mt-1 text-slate-500">
                Update your property information and photos.
              </p>
            </div>

            <UButton
              label="Back"
              icon="i-lucide-arrow-left"
              variant="soft"
              color="neutral"
              to="/seller/listings/manage"
            />
          </div>

          <div class="flex flex-col gap-4 md:flex-row md:items-center">
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
        </header>

        <CreateListingImagePreview :images="previewImages" />

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

            <div class="my-10 border-t border-slate-200" />

            <CreateListingUnits v-model="listingUnits" />

            <CreateListingAvailability v-model="availableFrom" />

            <div class="my-10 border-t border-slate-200" />

            <CreateListingPriceAdjustments v-model="priceAdjustments" />
          </div>

          <div>
            <UCard
              class="sticky top-6 bg-white shadow-lg ring-1 ring-slate-200"
            >
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
                  label="Update Listing"
                  size="xl"
                  block
                  class="bg-indigo-600 font-bold text-white hover:bg-indigo-700"
                  :loading="isSubmitting"
                  :disabled="isSubmitting || selectedUnits.length === 0"
                  @click="updateListing"
                />
              </div>
            </UCard>
          </div>
        </div>
      </div>
    </UContainer>
  </div>
</template>
