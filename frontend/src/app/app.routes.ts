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
import { RegisterComponent } from './register/register';
import { NotfoundComponent } from './notfound/notfound';
import { SearchResultsComponent } from './search-results/search-results';
import { AdminSummaryComponent } from './admin/admin-summary-component/admin-summary-component';
import { ServerUnreachableComponent } from './server-unreachable/server-unreachable';
import { ForbiddenComponent } from './forbidden/forbidden';

export const routes: Routes = [
    { path: '', component: WelcomeComponent , canActivate: [unAuthGuard]},
    { path: 'home', component: HomeComponent , canActivate: [authGuard]},
    { path: 'posts', component: PostsComponent , canActivate: [authGuard]},
    { path: 'signin', component: SigninComponent, canActivate: [unAuthGuard] },
    { path: 'register', component: RegisterComponent, canActivate: [unAuthGuard]},
    { path: 'posts/create', component: CreatePostComponent, canActivate: [authGuard] },
    { path: 'posts/:id', component: PostComponent, canActivate: [authGuard] },
    { path: 'users/:id', component: UserComponent, canActivate: [authGuard] },
    { path: 'myprofile', component: UserComponent, canActivate: [authGuard] },
    { path: 'searchresults', component: SearchResultsComponent, title: 'Search Results'},
    {
        path: 'adminpanel', component: AdminPanel, canActivate: [authGuard], children: [
            { path: 'summary', component: AdminSummaryComponent },
            { path: 'users', component: AdminUsersComponent },
            { path: 'posts', component: AdminPostsComponent },
            { path: 'reports', component: AdminReportsComponent },
            { path: '', redirectTo: 'summary', pathMatch: 'full' } // default tab
        ]
    },
    { path: 'unreachable', component: ServerUnreachableComponent},
    { path: 'forbidden', component: ForbiddenComponent },
    { path: '**', component: NotfoundComponent },

];

