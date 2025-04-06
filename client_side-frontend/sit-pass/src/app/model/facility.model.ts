export interface FacilityDTO {
    id: number;
    name: string;
    description: string;
    address: string;
    city: string;
    createdAt: string;
    rating: number;
    active: boolean;
    userId: number | null;
    imageIds: number[] | null;
    exerciseIds: number[] | null;
    disciplinesIds: number[] | null;
    workdays: number[] | null;
  }
  