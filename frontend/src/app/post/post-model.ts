import { CommentDto } from "../models/comment";

export interface Post {
  id:number;
  title: string;
  creator: User;
  description: string;
  createdAt: Date;
  media: String;
  reactions:Reaction[];
  comments:CommentDto[];
  likes:String;
  dislikes:String;
  owner:boolean;
}

interface User {
  id:number;
  username: String;
  photo:String;
}

interface Reaction {
  type:String;
}