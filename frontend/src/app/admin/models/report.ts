export interface Report {
  id: string;
  reporterId: string;
  reportedUserId?: string;
  reportedPostId?: string;
  reason: string;
  status: 'pending' | 'resolved' | 'dismissed';
  createdAt: string;
}