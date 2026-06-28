import type { Unit } from "~/types/UnitTypes";

export const unitTypes: Unit[] = [
  { label: 'Single room', value: 'single_room', maxGuests: 1, roomCount: 1 },
  { label: 'Double room', value: 'double_room', maxGuests: 2, roomCount: 1 },
  { label: 'Twin room', value: 'twin_room', maxGuests: 2, roomCount: 1 },
  { label: 'Queen room', value: 'queen_room', maxGuests: 2, roomCount: 1 },
  { label: 'King room', value: 'king_room', maxGuests: 2, roomCount: 1 },
  { label: 'Studio apartment', value: 'studio_apartment', maxGuests: 2, roomCount: 1 },
  { label: 'One-bedroom apartment', value: 'one_bedroom_apartment', maxGuests: 4, roomCount: 1 },
  { label: 'Two-bedroom apartment', value: 'two_bedroom_apartment', maxGuests: 6, roomCount: 2 },
  { label: 'Three-bedroom apartment', value: 'three_bedroom_apartment', maxGuests: 8, roomCount: 3 },
  { label: 'Family apartment', value: 'family_apartment', maxGuests: 6, roomCount: 2 },
  { label: 'Penthouse apartment', value: 'penthouse_apartment', maxGuests: 8, roomCount: 3 }
]