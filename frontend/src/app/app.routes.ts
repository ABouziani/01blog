import { Routes } from '@angular/router';
import { SigninComponent } from './signin/signin';
import { authGuard } from './guards/auth-guard';
import { ProfileComponent } from './profile/profile';
import { unAuthGuard } from './guards/unauth-guard';
import { HomeComponent } from './home/home';
import { CreatePostComponent } from './createpost/createpost';
import { PostComponent } from './post/post';
import { UserComponent } from './user/user';

export const routes: Routes = [
    { path: '', component: HomeComponent},
    { path: 'signin', component: SigninComponent, canActivate:[unAuthGuard] }, 
    { path: 'profile', component: ProfileComponent, canActivate: [authGuard]},
    { path: 'posts/create', component: CreatePostComponent, canActivate: [authGuard] },
    { path: 'posts/:id', component: PostComponent },
    { path: 'users/:id', component: UserComponent }
];

