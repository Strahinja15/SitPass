export interface UserDTO {
    id: number;
    email: string;
    name: string;
    surname?: string;
    phoneNumber?: string;
    birthday?: Date;
    address?: string;
    city?: string;
    role?: string;
    zipCode?: string;
    imageUrl?: string;
  }
  