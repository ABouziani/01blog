export interface CommentDto {
  postId:number;
  content:string;
  createdAt:string;
  user:User;
}

interface User {
  id:number;
  username:string;
  photo:String;
}