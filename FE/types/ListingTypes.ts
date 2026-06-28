export type ListingUnit = {
  id?: number;
  type: string;
  label: string;
  quantity: number;
  availableQuantity?: number;
  maxGuests?: number;
  pricePerNight: number;
  roomCount: number;
};

export type Listing = {
  id: number;
  title: string;
  rating: number; 
  location: string;
  latitude: number | null;
  longitude: number | null;
  description: string;

  lowestPrice: number;
  highestPrice: number;

  averageRating: number;
  reviewCount: number;

  images: string[];
  amenities: string[];

  availableFrom: string;
  units: ListingUnit[];

  status: string;
  sellerEmail: string;
  createdAt: string;
};

export type PriceAdjustment = {
  startDate: string
  endDate: string
  percent: number
}
