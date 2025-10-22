export interface NotificationItem {
  id: number;
  postId: number;
  postTitle: string;
  authorId: number;
  authorUsername: string;
  authorPhoto?: string | null;
  postCreatedAt: string; // ISO
  unread: boolean;
}
