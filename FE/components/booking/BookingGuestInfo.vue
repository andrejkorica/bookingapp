<script setup lang="ts">
const config = useRuntimeConfig();
import ListingLocationMap from "~/components/listings/ListingLocationMap.vue";
import type { ListingLocation } from "~/types/ListingTypes";
import type { User } from "~/types/UserTypes";

defineProps<{
  location: ListingLocation | null;
}>();

const guestInfo = defineModel<{
  name: string;
  surname: string;
  email: string;
  phoneNumber: string;
  travelingFrom: string;
  travelPurpose: string;
  arrivalTime: string;
  arrivalMethod: string;
  specialRequests: string;
  hasPets: boolean;
  needsParking: boolean;
  accessibilityRequirements: string;
}>({
  required: true,
});

const emit = defineEmits<{
  back: [];
  continue: [];
}>();

const travelPurposeOptions = [
  "Vacation",
  "Business",
  "Visiting family or friends",
  "Event",
  "Other",
];

const arrivalMethodOptions = ["Car", "Plane", "Bus", "Train", "Other"];

const canContinue = computed(() => {
  return Boolean(
    guestInfo.value.name &&
    guestInfo.value.surname &&
    guestInfo.value.email &&
    guestInfo.value.phoneNumber &&
    guestInfo.value.travelingFrom &&
    guestInfo.value.travelPurpose &&
    guestInfo.value.arrivalTime &&
    guestInfo.value.arrivalMethod
  );
});

async function fetchCurrentUser() {
  try {
    const user = await $fetch<User>(
      `${config.public.apiBase}/users/me`,
      {
        credentials: "include",
      },
    );

    guestInfo.value.name = user.name ?? "";
    guestInfo.value.surname = user.surname ?? "";
    guestInfo.value.email = user.email ?? "";
    guestInfo.value.phoneNumber = user.phoneNumber ?? "";
  } catch (error) {
    console.error(error);
  }
}

onMounted(fetchCurrentUser);
</script>

<template>
  <div class="space-y-6">
    <UCard>
      <template #header>
        <h2 class="text-xl font-semibold">Guest information</h2>
      </template>

      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <UFormField label="First name" required>
          <UInput v-model="guestInfo.name" class="w-full" />
        </UFormField>

        <UFormField label="Last name" required>
          <UInput v-model="guestInfo.surname" class="w-full" />
        </UFormField>

        <UFormField label="Email" required>
          <UInput v-model="guestInfo.email" type="email" class="w-full" />
        </UFormField>

        <UFormField label="Phone number" required>
          <UInput v-model="guestInfo.phoneNumber" type="tel" class="w-full" />
        </UFormField>
      </div>
    </UCard>

    <UCard>
      <template #header>
        <h2 class="text-xl font-semibold">Travel information</h2>
      </template>

      <div class="grid grid-cols-1 gap-4 md:grid-cols-2">
        <UFormField label="Traveling from" required>
          <UInput
            v-model="guestInfo.travelingFrom"
            placeholder="City or country"
            class="w-full"
          />
        </UFormField>
        <UFormField label="Purpose of travel" required>
          <USelect
            v-model="guestInfo.travelPurpose"
            :items="travelPurposeOptions"
            placeholder="Select purpose"
            class="w-full"
          />
        </UFormField>

        <UFormField label="Estimated arrival time" required>
          <UInput v-model="guestInfo.arrivalTime" type="time" class="w-full" />
        </UFormField>

        <UFormField label="How are you arriving?" required>
          <USelect
            v-model="guestInfo.arrivalMethod"
            :items="arrivalMethodOptions"
            placeholder="Select arrival method"
            class="w-full"
          />
        </UFormField>

        <UFormField label="Need parking?">
          <USwitch v-model="guestInfo.needsParking" />
        </UFormField>

        <UFormField label="Bringing pets?">
          <USwitch v-model="guestInfo.hasPets" />
        </UFormField>
      </div>
    </UCard>

    <UCard>
      <template #header>
        <h2 class="text-xl font-semibold">Additional information</h2>
      </template>

      <div class="space-y-4">
        <UFormField label="Special requests">
          <UTextarea
            v-model="guestInfo.specialRequests"
            placeholder="Late check-in, baby crib, extra towels..."
            :rows="4"
            class="w-full"
          />
        </UFormField>

        <UFormField label="Accessibility requirements">
          <UTextarea
            v-model="guestInfo.accessibilityRequirements"
            placeholder="Anything the host should know before your arrival..."
            :rows="3"
            class="w-full"
          />
        </UFormField>
      </div>
    </UCard>

    <UCard>
      <template #header>
        <h2 class="text-xl font-semibold">Getting there</h2>
      </template>

      <ListingLocationMap
        v-if="location"
        :location="location.location"
        :latitude="location.latitude"
        :longitude="location.longitude"
      />
    </UCard>

    <div
      class="sticky bottom-0 rounded-xl border border-slate-200 bg-white p-4 shadow-sm z-99999"
    >
      <div
        class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between"
      >
        <UButton
          label="Back"
          variant="ghost"
          color="neutral"
          icon="i-heroicons-arrow-left"
          @click="emit('back')"
        />

        <div class="flex flex-col items-end gap-2 sm:flex-row sm:items-center">
          <p class="text-sm text-slate-500">
            Review your guest details before continuing.
          </p>

          <UButton
            label="Continue to payment"
            color="primary"
            trailing-icon="i-heroicons-arrow-right"
            :disabled="!canContinue"
            class="px-6 text-white"
            @click="emit('continue')"
          />
        </div>
      </div>
    </div>
  </div>
</template>
