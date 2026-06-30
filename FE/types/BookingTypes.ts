

export type BookingStatus = "PENDING" | "CONFIRMED" | "CANCELLED";

export type BookedRange = {
  checkIn: string;
  checkOut: string;
};

export type BookingListing = {
  listingId: number;
  listingTitle: string;
  listingLocation: string;
  listingImage: string | null;
};


export type BookingDates = {
  checkIn: string;
  checkOut: string;
  nights: number;
};

export type BookingPrice = {
  pricePerNight: number;
  totalPrice: number;
};

export type BookingUnit = {
  unitType: string;
  unitLabel: string;
  quantity: number;
};

export type UserBooking = BookingListing &
  BookingDates &
  BookingPrice & {
    id: number;
    unitLabel: string;
    status: BookingStatus;
    createdAt: string;
  };

export type BookingDetails = BookingListing &
  BookingDates &
  BookingPrice & {
    id: number;

    units: BookingUnit[];

    guestName: string;
    guestSurname: string;
    guestEmail: string;
    guestPhoneNumber: string;

    travelPurpose: string;
    arrivalTime: string;
    arrivalMethod: string;
    specialRequests: string;
    hasPets: boolean;
    needsParking: boolean;
    accessibilityRequirements: string;

    billingAddress: string;

    agreedToRules: boolean;
    agreedToCancellationPolicy: boolean;
    confirmedInfoCorrect: boolean;

    status: BookingStatus;
    createdAt: string;
  };