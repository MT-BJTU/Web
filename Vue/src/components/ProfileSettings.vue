<template>
  <div class="profile-settings">
    <h2 class="profile-settings__title">个人设置</h2>
    <div class="profile-settings__avatar" @click="openAvatarModal">
      <img :src="avatar" alt="User Avatar" class="profile-settings__avatar-img" />
    </div>
    <form class="profile-settings__form">
      <div class="form-group">
        <label for="name" class="form-label">用户名</label>
        <input type="text" id="name" v-model="name" class="form-input" />
      </div>
      <div class="form-group">
        <label for="bio" class="form-label">个人简介</label>
        <textarea id="bio" v-model="bio" class="form-textarea" :maxlength="20"></textarea>
      </div>
      <div class="form-group">
        <label for="industry" class="form-label">所在行业</label>
        <input type="text" id="industry" v-model="industry" class="form-input" />
      </div>
      <div class="form-actions">
        <button type="submit" class="btn-primary" @click.prevent="saveSettings">保存</button>
        <button type="button" class="btn-secondary" @click="showChangePasswordModal">修改密码</button>
      </div>
    </form>
    <!-- 修改密码弹窗 -->
    <div v-if="showPasswordModal" class="password-modal">
      <div class="password-modal__content">
        <h3 class="password-modal__title">修改密码</h3>
        <div class="form-group">
          <label for="current-password" class="form-label">当前密码</label>
          <input type="password" id="current-password" v-model="currentPassword" class="form-input" />
        </div>
        <div class="form-group">
          <label for="new-password-modal" class="form-label">新密码</label>
          <input type="password" id="new-password-modal" v-model="newPasswordModal" class="form-input" />
        </div>
        <div class="form-group">
          <label for="confirm-password-modal" class="form-label">确认密码</label>
          <input type="password" id="confirm-password-modal" v-model="confirmPasswordModal" class="form-input" />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn-primary" @click.prevent="changePassword">确认</button>
          <button type="button" class="btn-secondary" @click="closeChangePasswordModal">取消</button>
        </div>
      </div>
    </div>
    <!-- 头像修改弹窗 -->
    <div v-if="showAvatarModal" class="avatar-modal">
      <div class="avatar-modal__content">
        <h3 class="avatar-modal__title">修改头像</h3>
        <div class="avatar-modal__upload">
          <label for="avatar-upload" class="avatar-modal__upload-label">
            <input type="file" id="avatar-upload" @change="handleAvatarChange" accept="image/*" />
            选择图片
          </label>
        </div>
        <button class="avatar-modal__cancel" @click="closeAvatarModal">取消</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      name: "",
      bio: "",
      industry: "",
      avatar: "",
      showAvatarModal: false,
      showPasswordModal: false,
      currentPassword: "",
      newPasswordModal: "",
      confirmPasswordModal: "",
      data:{
        userName: "",
        profile:"",
        trade:"",
        avatar:""
      }
    };
  },
  created() {
    this.$axios.get('/user/profile')
      .then(response => {
        const userData = response.data.data; // 假设响应以预期格式包含用户数据
        this.name = userData.userName;
        if(userData.profile==null){
          this.bio = "这个人很懒，什么都没有写~~";
        }
        else{
          this.bio=userData.profile;
        }
        this.industry = userData.trade;
        if(userData.avatar==null){
          this.avatar = "https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png";
        }else{
          this.avatar=userData.avatar;
        }
      })
      .catch(error => {
        console.error('获取用户信息失败', error);
      });
  },
  methods: {
    // 头像点击事件处理函数
    openAvatarModal() {
      this.showAvatarModal = true;
    },
    // 上传头像处理函数
    handleAvatarChange(event) {
      const file = event.target.files[0];
      const reader = new FileReader();
      reader.onload = (e) => {
        this.avatar = e.target.result;
      };
      reader.readAsDataURL(file);
      this.closeAvatarModal();
    },
    // 关闭头像弹窗
    closeAvatarModal() {
      this.showAvatarModal = false;
    },
    saveSettings() {
      this.data.userName=this.name;
      this.data.profile=this.bio;
      this.data.trade=this.industry;
      this.data.avatar=this.avatar;
      this.$axios.post('/user/save',this.data)
      .then(response=>{
        this.$message(response.data.msg)
        setTimeout(() => {
            window.location.reload();
          }, 500);
      })
    },
    showChangePasswordModal() {
      this.showPasswordModal = true;
    },
    closeChangePasswordModal() {
      this.showPasswordModal = false;
      this.currentPassword = "";
      this.newPasswordModal = "";
      this.confirmPasswordModal = "";
    },
    changePassword() {
      // 执行修改密码的逻辑
      if (this.newPasswordModal !== this.confirmPasswordModal) {
        this.$message("两次输入的密码不一致")
      }
      else{
      this.$axios.post('/user/changePassword',
      {oldPassword:this.currentPassword,
        newPassword:this.newPasswordModal})
      .then(response=>{
        this.$message(response.data.msg)
      })
      this.closeChangePasswordModal();
    }},
  },
};
</script>

<style scoped>
.profile-settings {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.profile-settings__title {
  font-size: 24px;
  margin-bottom: 20px;
}

.profile-settings__avatar {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  cursor: pointer;
}

.profile-settings__avatar-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 50%;
}

.profile-settings__form {
  flex-grow: 1;
}
.profile-settings__form {
  flex-grow: 1;
}


/* 头像修改弹窗样式 */
.avatar-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-modal__content {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 5px;
  max-width: 400px;
  width: 100%;
  text-align: center;
}

.avatar-modal__title {
  font-size: 18px;
  margin-bottom: 20px;
}

.avatar-modal__upload-label {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #007bff;
  color: #ffffff;
  border: none;
  cursor: pointer;
}

.avatar-modal__upload-label input[type="file"] {
  display: none;
}

.avatar-modal__cancel {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #f8f8f8;
  color: #333333;
  border: none;
  cursor: pointer;
}


.password-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.password-modal__content {
  background-color: #ffffff;
  padding: 20px;
  border-radius: 5px;
  max-width: 400px;
  width: 100%;
}

.password-modal__title {
  font-size: 18px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 16px;
  margin-bottom: 5px;
}

.form-input{
  font-size: 20px;
}


.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-primary {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #007bff;
  color: #ffffff;
  border: none;
  cursor: pointer;
  margin-right: 10px;
}

.btn-secondary {
  display: inline-block;
  padding: 10px 20px;
  font-size: 16px;
  background-color: #f8f8f8;
  color: #333333;
  border: none;
  cursor: pointer;
}
.form-textarea {
  width: 100%;
  padding: 8px;
  font-size: 14px;
  border: 1px solid #ccc;
  resize: none;
}
</style>




