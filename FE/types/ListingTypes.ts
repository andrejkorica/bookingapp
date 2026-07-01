export type PriceAdjustment = {
  startDate: string;
  endDate: string;
  percent: number;
};

export type ListingImage = {
  file?: File;
  previewUrl: string;
  existingUrl?: string;
  isUploading?: boolean;
};

export type ListingUnit = {
  id?: number;
  type: string;
  label: string;
  quantity: number;
  availableQuantity?: number;
  maxGuests?: number;
  roomCount: number;
  pricePerNight: number;
};

export type ListingLocation = {
  location: string;
  latitude: number | null;
  longitude: number | null;
};

export type ListingPrice = {
  lowestPrice: number;
  highestPrice: number;
};

export type ListingReviews = {
  rating: number;
  averageRating: number;
  reviewCount: number;
};

export type FavoriteListing = ListingLocation &
  ListingPrice & {
    id: number;

    title: string;
    description: string;

    rating: number;

    images: string[];
  };

export type Listing = ListingLocation &
  ListingPrice &
  ListingReviews & {
    id: number;

    title: string;
    description: string;

    images: string[];
    amenities: string[];

    availableFrom: string;

    units: ListingUnit[];
    priceAdjustments: PriceAdjustment[];

    status: string;
    sellerEmail: string;
    createdAt: string;
  };
