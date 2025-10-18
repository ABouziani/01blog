export interface PostDto {
  id: number;
  title: string;
  creatorId: String;
  creatorUsername: String;
  description: string;
  createdAt: Date;
  media: String;
  likes:String;
  dislikes:String;
  thumbnail:string;
  isowner:boolean;
}