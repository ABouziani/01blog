import { Routes } from '@angular/router';
import { SigninComponent } from './signin/signin';
import { authGuard } from './guards/auth-guard';
import { unAuthGuard } from './guards/unauth-guard';
import { HomeComponent } from './home/home';
import { CreatePostComponent } from './createpost/createpost';
import { PostComponent } from './post/post';
import { UserComponent } from './user/user';
import { AdminPanel } from './admin/adminpanel/adminpanel';
import { AdminUsersComponent } from './admin/users/users';
import { AdminPostsComponent } from './admin/posts/posts';
import { AdminReportsComponent } from './admin/reports/reports';
import { WelcomeComponent } from './welcome/welcome';
import { PostsComponent } from './posts/posts';

export const routes: Routes = [
    { path: '', component: WelcomeComponent , canActivate: [unAuthGuard]},
    { path: 'home', component: HomeComponent , canActivate: [authGuard]},
    { path: 'posts', component: PostsComponent , canActivate: [authGuard]},
    { path: 'signin', component: SigninComponent, canActivate: [unAuthGuard] },
    // { path: 'profile', component: ProfileComponent, canActivate: [authGuard]},
    { path: 'posts/create', component: CreatePostComponent, canActivate: [authGuard] },
    { path: 'posts/:id', component: PostComponent, canActivate: [authGuard] },
    { path: 'users/:id', component: UserComponent, canActivate: [authGuard] },
    { path: 'profile', component: UserComponent, canActivate: [authGuard] },
    {
        path: 'adminpanel', component: AdminPanel, canActivate: [authGuard], children: [
            { path: 'users', component: AdminUsersComponent },
            { path: 'posts', component: AdminPostsComponent },
            { path: 'reports', component: AdminReportsComponent },
            { path: '', redirectTo: 'users', pathMatch: 'full' } // default tab
        ]
    },

];

