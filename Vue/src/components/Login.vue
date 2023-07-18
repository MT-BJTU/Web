<template>
  <div class="login">
    <div class="login__card">
      <h2 class="login__title">用户登录</h2>
      <el-form ref="loginForm" :model="loginForm" label-width="80px" class="login__form">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="loginForm.userName" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" :loading="loginLoading" class="login__button">登录</el-button>
        </el-form-item>
        <div class="login__links">
          <span @click="doRegister">注册</span>
          <span @click="guestLogin">游客登录</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      loginForm: {
        userName: '',
        password: ''
      },
      loginLoading: false
    };
  },
  methods: {
    login() {
      this.$refs.loginForm.validate((valid) => {
        if (this.loginForm.userName === '' || this.loginForm.password === '') {
          this.$message('账号或密码不能为空');
        }
        if (valid) {
          this.loginLoading = true;
          // 发送登录请求
          this.$axios.post('/login', this.loginForm)
            .then(response => {
              this.$message(response.data.msg)
              if(response.data.code == 200){
              this.loginLoading = false;
              localStorage.setItem('token', response.data.data.token)
              this.$router.push('/')
            }
              else{
                localStorage.removeItem('token');
                this.loginLoading = false;
              }
            })
            .catch(error => {
              this.loginLoading = false;
              this.$message('网络故障')
            });
        } else {
          console.log('表单验证失败');
          return false;
        }
      });
    },
    doRegister() {
      this.$router.push('/register');
    },
    guestLogin() {
      // 游客登录逻辑...
    }
  }
};
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url(https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041747308.jpg);
  background-size: cover;
  background-position: center;
}

.login__card {
  width: 400px;
  background-color: rgba(245, 245, 245, 0.7); /* 背景色透明度调整为 0.9 */
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.login__title {
  text-align: center;
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.login__form {
  margin-top: 20px;
}

.login__button {
  margin: 0 20px 0 0; /* 横向居中并在右侧留出间距 */
  width: 100%;
  border-radius: 20px;
  background-color: #007aff;
}

.login__links {
  margin-top: 10px;
  text-align: center;
  font-size: 14px;
  color: #999;
  cursor: pointer;
}

.login__links span {
  margin: 0 10px;
}

.login__links span:hover {
  text-decoration: underline;
}

</style>