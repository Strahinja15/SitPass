import { CommentDTO } from "./comment.model";
import { RateDTO } from "./rate.model";

export interface ReviewDTO {
    id: number;
    createdAt: Date;
    exerciseCount: number;
    hidden: boolean;
    userId: number;
    facilityId: number;
    comments: CommentDTO[];
    rateDTO: RateDTO;  
}
