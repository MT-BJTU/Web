import Vue from 'vue'
import Router from 'vue-router'

import Login from '@/components/Login'
import Register from '@/components/Register'
import ForumLayout from '@/components/ForumLayout'
import ProfileSettings from '@/components/ProfileSettings'
import QuestionHelp from '@/components/QuestionHelp'
import QuestionAnswersComponent from '@/components/QuestionAnswersComponent.vue';
import MyQuestion from '@/components/MyQuestion.vue'
import TechDiscussion from '@/components/Articles.vue'
import MyFollower from '@/components/MyFollower.vue'
Vue.use(Router)

const router = new Router({
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/',
      name: 'forumLayout',
      component: ForumLayout,
      children: [
        {
          path: 'techDiscussion',
          name: 'techDiscussion',
          component: TechDiscussion
        },
        /*
        {
          path: 'ResourceShare',
          name: 'ResourceShare',
          component: ResourceShare
        },
        
  
        {
          path: 'MyAnswers',
          name: 'MyAnswers',
          component: MyAnswers
        },
        {
          path: 'MyArticles',
          name: 'MyArticles',
          component: MyArticles
        },*/
        {
          path: 'myQuestions',
          name: 'myQuestions',
          component: MyQuestion
        },
        {
          path: 'myFollowers',
          name: 'myFollowers',
          component: MyFollower
        },
        {
          path: '/question/:id',
          name: 'QuestionAnswers',
          component: QuestionAnswersComponent
        },
        {
          path: '/',
          name: 'QuestionHelp',
          component: QuestionHelp
        },
        {
          path: 'profileSettings',
          name: 'ProfileSettings',
          component: ProfileSettings
        }
      ]
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  // 检查token是否存在
  if (!token && (to.path !== '/login' && to.path !== '/register')) {
    next('/login');
  } else if (token && to.path === '/login') {
    // 如果有token且目标路由是登录页，则跳转到主页
    next('/');
  } else {
    // 其他情况，正常进行路由跳转
    next();
  }
});

export default router;