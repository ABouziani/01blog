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