<script setup lang="ts">
import ListingManagingCard from "~/components/listings/ListingManagingCard.vue";
import type { Listing } from "~/types/ListingTypes.js";

definePageMeta({
  middleware: "seller-guard",
});


const config = useRuntimeConfig();
const toast = useToast();

const listings = ref<Listing[]>([]);
const isLoading = ref(false);
const router = useRouter();

async function fetchListings() {
  isLoading.value = true;

  try {
    const data = await $fetch<Listing[]>(
      `${config.public.apiBase}/seller/listings`,
      {
        credentials: "include",
      },
    );

    listings.value = data.filter((listing) => listing.status === "APPROVED");
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to load listings.",
      color: "error",
    });
  } finally {
    isLoading.value = false;
  }
}

function previewListing(id: number) {
  navigateTo(`/listings/${id}`);
}

function editListing(id: number) {
  navigateTo(`/seller/listings/${id}/edit`);
}
function availability(id: number) {
  navigateTo(`/seller/listings/${id}/availability`);
}

async function deleteListing(id: number) {
  try {
    await $fetch(`${config.public.apiBase}/seller/listings/${id}/delete`, {
      method: "POST",
      credentials: "include",
    });

    listings.value = listings.value.filter((listing) => listing.id !== id);

    toast.add({
      title: "Listing deleted",
      description: "The listing was deleted successfully.",
      color: "success",
    });
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Error",
      description: "Failed to delete listing.",
      color: "error",
    });
  }
}

onMounted(fetchListings);
</script>

<template>
  <div class="min-h-screen bg-slate-50">
    <div class="container mx-auto px-4 py-10">
      <div class="mb-8 flex items-center justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-slate-900">Manage Listings</h1>

          <p class="mt-2 text-slate-600">
            Preview or edit your approved listings.
          </p>
        </div>

        <UButton
          label="Back"
          icon="i-lucide-arrow-left"
          variant="soft"
          color="neutral"
          class="shrink-0"
          @click="navigateTo('/seller')"
        />
      </div>

      <div v-if="isLoading" class="py-16 text-center text-slate-500">
        Loading listings...
      </div>

      <div
        v-else-if="listings.length === 0"
        class="rounded-2xl border border-dashed border-slate-300 bg-white p-12 text-center"
      >
        <UIcon
          name="i-lucide-building-2"
          class="mx-auto mb-4 size-10 text-slate-400"
        />

        <h2 class="text-lg font-semibold text-slate-900">
          No approved listings yet
        </h2>

        <p class="mt-2 text-sm text-slate-500">
          Only approved listings can be managed here.
        </p>
      </div>

      <div
        v-else
        class="grid items-stretch gap-6 md:grid-cols-2 xl:grid-cols-3"
      >
        <ListingManagingCard
          v-for="listing in listings"
          :key="listing.id"
          :listing="listing"
          @preview="previewListing"
          @edit="editListing"
          @availability="availability"
          @delete="deleteListing"
        />
      </div>
    </div>
  </div>
</template>
