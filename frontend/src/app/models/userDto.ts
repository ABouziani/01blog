import { PostDto } from './postDto';

export interface UserDto {
  id: number;
  username: string;
  email: string;
  bio: string;
  photo: string;
  createdAt: string; // or Date
  role: string;
  posts: PostDto[];
  alreadyReported: boolean;
  alreadyFollowed: boolean;
  owner: boolean;
  followers: User[];
  following: User[];
}

interface User {
  id: number;
  username: string;
  photo: string;
}