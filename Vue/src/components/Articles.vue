<template>
    <div class="article-list">
      <el-card
        v-for="article in displayedArticles"
        :key="article.articleId"
        :class="['article-card', getRandomColor()]"
        @click.stop="navigateToAnswers(article.articleId)"
      >
        <!-- 问题卡片的内容 -->
        <div class="article-avatar-username">
          <div class="article-user-avatar">
            <img :src="article.user.avatar" alt="User Avatar" class="user-avatar">
          </div>
          <div class="article-username">{{ article.user.userName }}</div>
        </div>
        <div class="article-details">
          <div class="article-title">{{ article.title }}</div>
          <div class="article-description">{{ article.description }}</div>
          <div class="article-actions">
            <div class="article-actions-left">
              <el-badge :value="article.answers">
                <el-button type="text" icon="el-icon-edit" class="answer-button" @click.stop="navigateToAnswers(article.articleId)"></el-button>
              </el-badge>
              <span class="article-answers">{{ article.answers }} 评论</span>
            </div>
            <div class="article-actions-right">
              <span class="article-time">发布时间: {{ article.time }}</span>
            </div>
          </div>
        </div>
      </el-card>
  
      <el-button type="primary" @click="showarticleDialog" class="ask-button">随笔</el-button>
  
      <el-dialog
        title="随笔"
        :visible.sync="dialogVisible"
        :close-on-click-modal="false"
        :show-close="false"
      >
        <el-form ref="articleForm" :model="newarticle" label-width="80px">
          <el-form-item label="文章标题" required>
            <el-input v-model="newarticle.title" autocomplete="off" placeholder="请输入文章标题"></el-input>
          </el-form-item>
          <el-form-item label="文章内容" required>
            <el-input v-model="newarticle.description" type="textarea" rows="4" placeholder="请输入文章主体"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitarticle">提交</el-button>
          </el-form-item>
        </el-form>
      </el-dialog>
  
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="articles.length"
        @current-change="handlePageChange"
        layout="prev, pager, next"
      ></el-pagination>
    </div>
  </template>
  
  <script>
  import { ElBadge } from 'element-ui';
  export default {
    components: {
      'el-badge': ElBadge,
    },
    data() {
      return {
        articles: [
          {
            articleId: '',
            title: '',
            description: '',
            user: {
              userName: '',
              avatar: '',
            },
            time: '',
            answers: '',
          },
          // 其他问题对象...
        ],
        currentPage: 1,
        pageSize: 5,
        dialogVisible: false,
        newarticle: {
          title: '',
          description: '',
        },
      };
    },
    computed: {
      displayedarticles() {
        const startIndex = (this.currentPage - 1) * this.pageSize;
        const endIndex = startIndex + this.pageSize;
        return this.articles.slice(startIndex, endIndex);
      },
    },
    methods: {
      navigateToAnswers(articleId) {
        this.$router.push({ name: 'articleAnswers', params: { id: articleId } });
      },
      getRandomColor() {
        const colors = [
          'article-color-1',
          'article-color-2',
          'article-color-3',
          'article-color-4',
          'article-color-5',
        ];
        return colors[Math.floor(Math.random() * colors.length)];
      },
      handlePageChange(currentPage) {
        this.currentPage = currentPage;
      },
      showarticleDialog() {
        this.dialogVisible = true;
      },
      submitarticle() {
        // 构造问题对象
        const articleData = {
          title: this.newarticle.title,
          description: this.newarticle.description,
        };
  
        // 发送 POST 请求将问题提交到服务器
        this.$axios
          .post('/submit-article', articleData)
          .then((response) => {
            // 提交成功的处理逻辑
            console.log('文章提交成功:', response.data);
  
            // 刷新问题列表，重新获取问题数据
            this.$axios
              .get('/articles')
              .then((response) => {
                this.articles = response.data.data;
                this.articles.forEach((article) => {
                  if (!article.user.avatar) {
                    article.user.avatar =
                      'https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png';
                  }
                });
              })
              .catch((error) => {
                console.error('获取文章列表失败:', error);
              });
  
            // 给出成功提示
            this.$message(response.data.msg);
          })
          .catch((error) => {
            // 提交失败的处理逻辑
            console.error('文章提交失败:', error);
            this.$message.error('文章提交失败，请重试！');
          })
          .finally(() => {
            // 关闭对话框
            this.dialogVisible = false;
          });
      },
    },
    created() {
      // 组件加载时发送请求获取问题列表
      this.$axios
        .get('/articles')
        .then((response) => {
          this.articles = response.data.data;
          // 设置默认头像
          this.articles.forEach((article) => {
            if (!article.user.avatar) {
              article.user.avatar =
                'https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png';
            }
          });
        })
        .catch((error) => {
          console.error('获取文章列表失败:', error);
        });
    },
  };
  </script>
  
  <style scoped>
  .article-list {
    max-width: 800px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .article-card {
    margin-bottom: 20px;
    padding: 20px;
    color: black;
    position: relative;
  }
  
  .article-avatar-username {
    position: absolute;
    top: 10px;
    left: 10px;
    display: flex;
    align-items: flex-start;
  }
  
  .article-user-avatar {
    margin-right: 10px;
  }
  
  .user-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    object-fit: cover;
  }
  
  .article-username {
    font-size: 14px;
    color: #666;
  }
  
  .article-title {
    font-size: 16px;
    font-weight: bold;
    margin-bottom: 10px;
  }
  
  .article-description {
    margin-bottom: 10px;
  }
  
  .article-actions {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .article-actions-left {
    display: flex;
    align-items: center;
  }
  
  .article-answers {
    font-size: 14px;
    color: #666;
    margin-left: 5px;
  }
  
  .article-time {
    font-size: 14px;
    color: #666;
  }
  
  .el-button {
    margin-top: 10px;
  }
  
  .ask-button {
    position: fixed;
    right: 20px;
    bottom: 20px;
    z-index: 9999;
  }
  
  .answer-button {
    font-size: 18px !important;
  }
  
  .article-color-1 {
    background-color: #f2f2f2;
  }
  
  .article-color-2 {
    background-color: #e6f7ff;
  }
  
  .article-color-3 {
    background-color: #f0f9ff;
  }
  
  .article-color-4 {
    background-color: #fffbe6;
  }
  
  .article-color-5 {
    background-color: #f0f0f5;
  }
  </style>
  