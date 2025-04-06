export interface CommentDTO {
    id: number;
    text: string;
    createdAt: Date;
    reviewId: number;
    userId: number;
}