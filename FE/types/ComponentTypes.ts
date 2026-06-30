import type { DateValue } from "@internationalized/date";

export type DateRangeValue = {
  start: DateValue | undefined;
  end: DateValue | undefined;
};

export type LocationSuggestion = {
  display_name: string;
  lat: string;
  lon: string;
};

export type GuestInfoFilter = {
  adults: number;
  children: number;
  rooms: number;
};

export type PriceRange = {
  min: number | null;
  max: number | null;
};
