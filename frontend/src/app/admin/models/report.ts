import { PostDto } from "../../models/postDto";

export interface Report {
  id: string;
  reporterId: string;
  reporterUsername: string;
  reportedUserId?: string;
  reportedUserUsername?: string;
  reportedPostId?: string;
  reason: string;
  status: 'pending' | 'resolved' | 'dismissed';
  createdAt: string;
}

export interface PostReport {
  id: string;
  reporterId: string;
  reporterUsername: string;
  reportedPost: PostDto;
  reason: string;
  status: 'pending' | 'resolved' | 'dismissed';
  createdAt: string;
}