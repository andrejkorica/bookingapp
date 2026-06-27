export type UnitType = {
  label: string
  value: string
  maxGuests: number
}

export const unitTypes: UnitType[] = [
  { label: 'Single room', value: 'single_room', maxGuests: 1 },
  { label: 'Double room', value: 'double_room', maxGuests: 2 },
  { label: 'Twin room', value: 'twin_room', maxGuests: 2 },
  { label: 'Queen room', value: 'queen_room', maxGuests: 2 },
  { label: 'King room', value: 'king_room', maxGuests: 2 },
  { label: 'Studio apartment', value: 'studio_apartment', maxGuests: 2 },
  { label: 'One-bedroom apartment', value: 'one_bedroom_apartment', maxGuests: 4 },
  { label: 'Two-bedroom apartment', value: 'two_bedroom_apartment', maxGuests: 6 },
  { label: 'Three-bedroom apartment', value: 'three_bedroom_apartment', maxGuests: 8 },
  { label: 'Family apartment', value: 'family_apartment', maxGuests: 6 },
  { label: 'Penthouse apartment', value: 'penthouse_apartment', maxGuests: 8 }
]