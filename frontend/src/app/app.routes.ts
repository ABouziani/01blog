import { Routes } from '@angular/router';
import { SigninComponent } from './signin/signin';
import { authGuard } from './guards/auth-guard';
import { ProfileComponent } from './profile/profile';
import { unAuthGuard } from './guards/unauth-guard';
import { HomeComponent } from './home/home';
import { CreatePostComponent } from './createpost/createpost';
import { PostComponent } from './post/post';
import { UserComponent } from './user/user';
import { AdminPanel } from './admin/adminpanel/adminpanel';
import { AdminUsersComponent } from './admin/users/users';
import { AdminPostsComponent } from './admin/posts/posts';
import { AdminReportsComponent } from './admin/reports/reports';

export const routes: Routes = [
    { path: '', component: HomeComponent },
    { path: 'signin', component: SigninComponent, canActivate: [unAuthGuard] },
    // { path: 'profile', component: ProfileComponent, canActivate: [authGuard]},
    { path: 'posts/create', component: CreatePostComponent, canActivate: [authGuard] },
    { path: 'posts/:id', component: PostComponent },
    { path: 'users/:id', component: UserComponent },
    { path: 'profile', component: UserComponent },
    {
        path: 'adminpanel', component: AdminPanel, children: [
            { path: 'users', component: AdminUsersComponent },
            { path: 'posts', component: AdminPostsComponent },
            { path: 'reports', component: AdminReportsComponent },
            { path: '', redirectTo: 'users', pathMatch: 'full' } // default tab
        ]
    },

];

