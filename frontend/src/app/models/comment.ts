export interface CommentDto {
  id:number;
  postId:number;
  content:string;
  createdAt:string;
  user:User;
  owner:boolean;
}

interface User {
  id:number;
  username:string;
  photo:String;
}