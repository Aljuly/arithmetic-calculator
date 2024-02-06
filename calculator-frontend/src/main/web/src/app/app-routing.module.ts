import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AuthGuard } from './guards/auth.guard';
import { RoleGuard } from './guards/role.guard';
import { adminRole } from './helper/fake-data/fake_roles';
import { MemberListComponent } from './components/member-list/member-list.component';
import { RoleListComponent } from './components/role-list/role-list.component';
import { UserListComponent } from './components/user-list/user-list.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { NoAccessComponent } from './components/no-access/no-access.component';
import { ProfileHomeComponent } from './components/profile-home/profile-home.component';

const appRoutes: Routes = [
  {
      path: '',
      component: HomeComponent,
      data: {

      }
  },
  // {
  //     path: 'oauth-callback/:provider',
  //     component: SocialAuthComponent,
  // },
  {path: 'profile', component: ProfileHomeComponent, canActivate: [AuthGuard]},
//   {path: 'register', component: RegisterComponent},
  {path: 'no-access', component: NoAccessComponent},
  {
      path: 'users',
      component: UserListComponent,
      canActivate: [ RoleGuard ],
      data: {
          expectedRoles: [ adminRole ],
          breadcrumb: 'Users'
      },
  },
  {
      path: 'members',
      component: MemberListComponent,
      canActivate: [ RoleGuard ],
      data: {
          expectedRoles: [ adminRole ],
          breadcrumb: 'Members'
      }
  },
  /*
  {
      path: 'users/:username',
      component: UserViewComponent,
      data: {
          breadcrumb: 'Users'
      }
  },
  */
  {
      path: 'roles',
      component: RoleListComponent,
      canActivate: [RoleGuard],
      data: {
          expectedRoles: [ adminRole ],
          breadcrumb: 'Roles'
      }
  },
  // {
  //     path: 'posts',
  //     component: PostsComponent,
  //     canActivate: [RoleGuard],
  //     data: {
  //         expectedRoles: [ moderatorRole, adminRole ], // TODO: maybe get role from service when app is started
  //         breadcrumb: 'Posts'
  //     }
  // },
  /*
      {path: 'manage/!**', canActivate: 'current-user has admin authority'},
      {path: 'manage/users', component: UsersComponent},
      {path: 'manage/students', component: StudentsComponent},
      {path: 'manage/mentors', component: MentorsComponent},
  */
  {
      path: 'manage',
      // component: ManageComponent,
      canActivate: [RoleGuard],
      data: {
          expectedRoles: [adminRole],
          breadcrumb: 'Manage'
      },
      children: [
          {
              path: 'users_',
              component: UserListComponent,
              data: {
                  expectedRoles: [adminRole],
                  breadcrumb: 'Users'
              }
          },
          {
              path: 'students',
              component: UserListComponent,
              data: {
                  expectedRoles: [adminRole],
                  breadcrumb: 'Users'
              }
          },
          {
              path: 'mentors',
              component: UserListComponent,
              data: {
                  expectedRoles: [adminRole],
                  breadcrumb: 'Users'
              }
          }
      ]
  },
  // {
  //     path: 'followers',
  //     component: GithubFollowersComponent,
  //     canActivate: [AuthGuard],
  //     data: {
  //         breadcrumb: 'Followers'
  //     }
  // },

  // {
  //     path: 'followers/:id/:login',
  //     component: GithubProfileComponent,
  //     data: {
  //         breadcrumb: 'Followers'
  //     }
  // },

  // {
  //     path: 'unit',
  //     component: CoursePageComponent,
  //     data: {
  //         breadcrumb: 'unitPage'
  //     }
  // },
  // otherwise redirect to 404
  {
      path: '**',
      component: NotFoundComponent
  }
];

export const ROUTING = RouterModule.forRoot(appRoutes);
