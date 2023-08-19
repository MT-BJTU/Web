<template>
  <div class="forum">
    <div class="sidebar">
      <div class="sidebar__header">
        <img :src="userAvatar" alt="用户头像" class="sidebar__avatar" />
      </div>
      <el-menu :default-openeds="['1', '2', '3']" class="sidebar__menu">
        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-message"></i>
            <span>帖子分类</span>
          </template>
          <el-menu-item index="1-1" @click="navigateTo('问题求助')">问题求助</el-menu-item>
          <el-menu-item index="1-2" @click="navigateTo('技术讨论')">技术讨论</el-menu-item>
        </el-submenu>
        <el-submenu v-if="userId != 0" index="2">
          <template slot="title">
            <i class="el-icon-user"></i>
            <span>个人中心</span>
          </template>
          <el-menu-item index="2-1" @click="navigateTo('我的问题')">我的问题</el-menu-item>
          <el-menu-item index="2-2" @click="navigateTo('我的关注')">我的关注</el-menu-item>
          <el-menu-item index="2-3" @click="navigateTo('我的文章')">我的文章</el-menu-item>
          <el-menu-item index="2-4" @click="navigateTo('我的收藏')">我的收藏</el-menu-item>
          <el-menu-item index="2-5" @click="navigateTo('个人设置')">个人设置</el-menu-item>
        </el-submenu>
        <el-menu-item index="3" @click="logout">
          <i class="el-icon-switch-button"></i>
          <span>登出</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="main">
      <router-view></router-view>
    </div>
  </div>
</template>


<script>
export default {
  data() {
    return {
      userId:'',
      userAvatar: '',
      isSidebarHidden: false,
      isSidebarCollapsed: false
    };
  },
    created() {
    this.$axios.get('/user/profile')
      .then(response => {
        const userData = response.data.data; 
        this.userId=userData.userId
        if(userData.avatar==null){
          this.userAvatar = "https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png";
        }else{
          this.userAvatar=userData.avatar;
        }
      })
      .catch(error => {
        console.error('获取用户信息失败', error);
      });
  },
  methods: {
    toggleSidebar() {
      this.isSidebarHidden = !this.isSidebarHidden;
      this.isSidebarCollapsed = !this.isSidebarCollapsed;
    },
    logout() {
      this.$axios.get('/logout')
      localStorage.removeItem('token');
      this.$router.push('/login');
    },
    navigateTo(page) {
      if (page === '技术讨论') {
        this.$router.push('/techDiscussion');
      } else if(page==='我的关注'){
        this.$router.push('/myFollowers');
      } else if (page === '问题求助') {
        this.$router.push('/');
      } else if (page === '我的问题') {
        this.$router.push('/myQuestions');
      } else if (page === '我的收藏') {
        this.$router.push('/my-collect');
      } else if (page === '我的文章') {
        this.$router.push('/my-articles');
      } else if (page === '个人设置') {
        this.$router.push('/profileSettings');
      }
    }
  }
};
</script>

<style scoped>
.forum {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 240px;
  background-color: #f5f5f7;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding-top: 30px;
  box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
}

.sidebar__header {
  margin-bottom: 20px;
}

.sidebar__avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.sidebar__menu {
  width: 100%;
}

.main {
  flex: 1;
}

.sidebar-toggle {
  display: none;
}

.main--sidebar-hidden .page-content {
  margin-left: 0;
}
</style>