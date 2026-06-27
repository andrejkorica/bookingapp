<script setup lang="ts">
import type { DateValue } from "@internationalized/date";
import { getLocalTimeZone, parseDate } from "@internationalized/date";
import BookingSteps from "~/components/booking/BookingSteps.vue";
import BookingUnitSelector from "~/components/booking/BookingUnitSelector.vue";
import BookingDateSelector from "~/components/booking/BookingDateSelector.vue";
import BookingPriceSummary from "~/components/booking/BookingPriceSummary.vue";
import BookingGuestInfo from "~/components/booking/BookingGuestInfo.vue";
import BookingPaymentForm from "~/components/booking/BookingPaymentForm.vue";
import type { BookedRange } from "~/types/BookingTypes";

definePageMeta({
  layout: "default",
});

type DateRangeValue = {
  start: DateValue | undefined;
  end: DateValue | undefined;
};

type ListingUnit = {
  id?: number;
  type: string;
  label: string;
  quantity: number;
  availableQuantity?: number;
  maxGuests?: number;
  pricePerNight: number;
};

type PriceAdjustment = {
  id: number;
  startDate: string;
  endDate: string;
  percent: number;
};

type Listing = {
  id: number;
  title: string;
  location: string;
  latitude: number | null;
  longitude: number | null;
  availableFrom: string;
  units: ListingUnit[];
  priceAdjustments: PriceAdjustment[];
};

const route = useRoute();
const config = useRuntimeConfig();
const toast = useToast();

const step = ref(1);
const listing = ref<Listing | null>(null);
const availableUnits = ref<ListingUnit[]>([]);
const isLoading = ref(false);
const isLoadingAvailableUnits = ref(false);
const isSubmittingBooking = ref(false);
const bookedRanges = ref<BookedRange[]>([]);

const selectedUnits = ref<Record<string, number>>({});

const dateRange = shallowRef<DateRangeValue>({
  start: undefined,
  end: undefined,
});

const guestInfo = reactive({
  name: "",
  surname: "",
  email: "",
  phoneNumber: "",
  travelPurpose: "",
  arrivalTime: "",
  arrivalMethod: "",
  accessibilityRequirements: "",
  specialRequests: "",
  hasPets: false,
  needsParking: false,
  travelingFrom: "",
});

const paymentInfo = reactive({
  cardholderName: "",
  cardNumber: "",
  expiryDate: "",
  cvc: "",
  billingAddress: "",
  agreedToRules: false,
  agreedToCancellationPolicy: false,
  confirmedInfoCorrect: false,
});

async function fetchBookedRanges(listingId: number) {
  try {
    bookedRanges.value = await $fetch<BookedRange[]>(
      `${config.public.apiBase}/bookings/${listingId}/booked-ranges`,
      {
        credentials: "include",
      },
    );
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Booked dates failed",
      description: "Could not load unavailable dates.",
      color: "error",
    });

    bookedRanges.value = [];
  }
}

const availableFromDate = computed(() => {
  if (!listing.value?.availableFrom) {
    return undefined;
  }

  return parseDate(listing.value.availableFrom);
});

const unitOptions = computed(() => {
  const sourceUnits = availableUnits.value.length
    ? availableUnits.value
    : (listing.value?.units ?? []);

  return sourceUnits
    .filter((unit) => {
      const quantity =
        unit.availableQuantity !== undefined
          ? Number(unit.availableQuantity)
          : Number(unit.quantity);

      return quantity > 0;
    })
    .map((unit) => ({
      value: unit.type,
      label: unit.label,
      pricePerNight: Number(unit.pricePerNight),
      quantity:
        unit.availableQuantity !== undefined
          ? Number(unit.availableQuantity)
          : Number(unit.quantity),
      maxGuests: unit.maxGuests,
    }));
});

const selectedUnitItems = computed(() => {
  return unitOptions.value
    .map((unit) => ({
      ...unit,
      selectedQuantity: selectedUnits.value[unit.value] ?? 0,
    }))
    .filter((unit) => unit.selectedQuantity > 0);
});

const selectedUnitsLabel = computed(() => {
  if (!selectedUnitItems.value.length) {
    return "Not selected";
  }

  return selectedUnitItems.value
    .map((unit) => `${unit.selectedQuantity} × ${unit.label}`)
    .join(", ");
});

const nights = computed(() => {
  if (!dateRange.value.start || !dateRange.value.end) {
    return 0;
  }

  const start = dateRange.value.start.toDate(getLocalTimeZone());
  const end = dateRange.value.end.toDate(getLocalTimeZone());
  const diff = end.getTime() - start.getTime();

  return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)));
});

function isDateInsideAdjustment(date: Date, adjustment: PriceAdjustment) {
  const currentDate = date.toISOString().slice(0, 10);

  return (
    currentDate >= adjustment.startDate && currentDate <= adjustment.endDate
  );
}

const nightlyPrices = computed(() => {
  if (!dateRange.value.start || !dateRange.value.end) {
    return [];
  }

  if (!selectedUnitItems.value.length) {
    return [];
  }

  const prices: number[] = [];

  const start = dateRange.value.start.toDate(getLocalTimeZone());
  const end = dateRange.value.end.toDate(getLocalTimeZone());

  const current = new Date(start);

  while (current < end) {
    let priceForNight = selectedUnitItems.value.reduce((sum, unit) => {
      return sum + unit.pricePerNight * unit.selectedQuantity;
    }, 0);

    listing.value?.priceAdjustments?.forEach((adjustment) => {
      if (isDateInsideAdjustment(current, adjustment)) {
        priceForNight += priceForNight * (Number(adjustment.percent) / 100);
      }
    });

    prices.push(priceForNight);
    current.setDate(current.getDate() + 1);
  }

  return prices;
});

const finalPricePerNight = computed(() => {
  if (!nightlyPrices.value.length) {
    return 0;
  }

  const total = nightlyPrices.value.reduce((sum, price) => sum + price, 0);

  return Math.round(total / nightlyPrices.value.length);
});

const totalPrice = computed(() => {
  return Math.round(nightlyPrices.value.reduce((sum, price) => sum + price, 0));
});

const canContinue = computed(() => {
  return selectedUnitItems.value.length > 0 && nights.value > 0;
});

const checkInLabel = computed(() => {
  if (!dateRange.value.start) {
    return "Not selected";
  }

  return dateRange.value.start.toDate(getLocalTimeZone()).toLocaleDateString();
});

const checkOutLabel = computed(() => {
  if (!dateRange.value.end) {
    return "Not selected";
  }

  return dateRange.value.end.toDate(getLocalTimeZone()).toLocaleDateString();
});

async function fetchAvailableUnits(listingId: number) {
  isLoadingAvailableUnits.value = true;

  try {
    availableUnits.value = await $fetch<ListingUnit[]>(
      `${config.public.apiBase}/listings/${listingId}/available-units`,
      {
        credentials: "include",
      },
    );

    selectedUnits.value = {};
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Availability failed",
      description: "Could not load available units.",
      color: "error",
    });

    availableUnits.value = listing.value?.units ?? [];
  } finally {
    isLoadingAvailableUnits.value = false;
  }
}

async function fetchListing() {
  const listingId = Array.isArray(route.query.listingId)
    ? route.query.listingId[0]
    : route.query.listingId;

  if (!listingId) {
    console.error("Missing listingId in route query");
    return;
  }

  isLoading.value = true;

  try {
    listing.value = await $fetch<Listing>(
      `${config.public.apiBase}/listings/${listingId}`,
      {
        credentials: "include",
      },
    );

    await Promise.all([
      fetchAvailableUnits(listing.value.id),
      fetchBookedRanges(listing.value.id),
    ]);
  } catch (error) {
    console.error(error);
  } finally {
    isLoading.value = false;
  }
}

function goNext() {
  if (!canContinue.value) {
    return;
  }

  step.value = 2;
}

function goBackToDates() {
  step.value = 1;
}

function goToPayment() {
  step.value = 3;
}

function goBackToGuestInfo() {
  step.value = 2;
}

async function confirmBooking() {
  if (!listing.value || !dateRange.value.start || !dateRange.value.end) {
    toast.add({
      title: "Booking failed",
      description: "Something went wrong. Please check your booking details.",
      color: "error",
    });

    return;
  }

  isSubmittingBooking.value = true;

  try {
    await $fetch(`${config.public.apiBase}/bookings`, {
      method: "POST",
      credentials: "include",
      body: {
        listingId: listing.value.id,

        units: selectedUnitItems.value.map((unit) => ({
          unitType: unit.value,
          quantity: unit.selectedQuantity,
        })),

        checkIn: dateRange.value.start.toString(),
        checkOut: dateRange.value.end.toString(),

        guestName: guestInfo.name,
        guestSurname: guestInfo.surname,
        guestEmail: guestInfo.email,
        guestPhoneNumber: guestInfo.phoneNumber,

        travelPurpose: guestInfo.travelPurpose,
        arrivalTime: guestInfo.arrivalTime,
        arrivalMethod: guestInfo.arrivalMethod,
        specialRequests: guestInfo.specialRequests,
        hasPets: guestInfo.hasPets,
        needsParking: guestInfo.needsParking,
        accessibilityRequirements: guestInfo.accessibilityRequirements,

        billingAddress: paymentInfo.billingAddress,
        agreedToRules: paymentInfo.agreedToRules,
        agreedToCancellationPolicy: paymentInfo.agreedToCancellationPolicy,
        confirmedInfoCorrect: paymentInfo.confirmedInfoCorrect,
      },
    });

    toast.add({
      title: "Booking request sent",
      description: "Your booking is waiting for confirmation.",
      color: "success",
    });

    await navigateTo("/bookings");
  } catch (error) {
    console.error(error);

    toast.add({
      title: "Booking failed",
      description: "Something went wrong. Please try again.",
      color: "error",
    });
  } finally {
    isSubmittingBooking.value = false;
  }
}

onMounted(fetchListing);
</script>

<template>
  <div class="min-h-screen bg-slate-50 text-slate-900">
    <UContainer class="py-10">
      <div v-if="isLoading" class="py-16 text-center text-slate-500">
        Loading booking...
      </div>

      <div v-else>
        <div class="mb-8">
          <h1 class="text-3xl font-bold">Complete your booking</h1>

          <p class="mt-2 text-slate-600">
            Choose the unit type and dates for your stay.
          </p>
        </div>

        <BookingSteps :step="step" />

        <div v-if="step === 1" class="grid grid-cols-1 gap-8 lg:grid-cols-3">
          <div class="space-y-6 lg:col-span-2">
            <div
              v-if="isLoadingAvailableUnits"
              class="rounded-xl border border-slate-200 bg-white p-4 text-sm text-slate-500"
            >
              Loading available units...
            </div>

            <BookingUnitSelector
              v-else
              v-model="selectedUnits"
              :unit-options="unitOptions"
            />

            <BookingDateSelector
              v-model="dateRange"
              :min-date="availableFromDate || undefined"
              :booked-ranges="bookedRanges"
            />
          </div>

          <BookingPriceSummary
            :selected-unit-label="selectedUnitsLabel"
            :check-in="checkInLabel"
            :check-out="checkOutLabel"
            :nights="nights"
            :final-price-per-night="finalPricePerNight"
            :total-price="totalPrice"
            :can-continue="canContinue"
            @continue="goNext"
          />
        </div>

        <div v-else-if="step === 2">
          <BookingGuestInfo
            v-model="guestInfo"
            :listing="listing"
            @back="goBackToDates"
            @continue="goToPayment"
          />
        </div>

        <div v-else-if="step === 3">
          <BookingPaymentForm
            v-model="paymentInfo"
            :loading="isSubmittingBooking"
            @back="goBackToGuestInfo"
            @confirm="confirmBooking"
          />
        </div>
      </div>
    </UContainer>
  </div>
</template>
