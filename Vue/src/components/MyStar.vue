<template>
    <div class="article-list">
      <div
        v-for="article in displayedarticles"
        :key="article.articleId"
        :class="['article-card', getRandomColor()]"
      >
        <div class="article-avatar-username">
          <div class="article-user-avatar">
            <img :src="article.user.avatar" alt="User Avatar" class="user-avatar">
          </div>
          <div class="article-username">{{ article.user.userName }}</div>
        </div>
        <div class="article-details">
          <div class="article-title">{{ article.title }}</div>
          <div class="article-content">     
             <markdown-collapse :content="article.content" />
          </div>
          <div class="article-actions">
            <div class="article-actions-left">
              <el-badge>
                <el-button
                  type="text" @click.stop="toggleStarArticle(article)"
                >
              <i class="el-icon-star-off" v-if="!article.stared"></i>
              <i class="el-icon-star-on" v-else></i>
                </el-button>
              </el-badge>
              <span class="star-count">{{ article.stars }} 收藏</span>
            </div> 
            <div class="article-actions-right">
              <span class="article-time">发布时间: {{ article.releaseTime }}</span>
              <i v-if="article.userID===userId||userId===1" class="el-icon-delete delete-icon" @click.stop="deletearticle(article.essayID)"></i>
            </div>
          </div>
        </div>
    </div>
    <div class="pagination-space"></div>
    <el-pagination
      v-if="articles.length !== 0"
      class="pagination-container"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="articles.length"
      @current-change="handlePageChange"
    ></el-pagination>
    <el-alert
          v-else
          title="您还没收藏过文章"
          type="info"
          show-icon
          class="no-results-alert"
          :closable="false"
        />
    </div>
  </template>
    
    <script>
    import MarkdownCollapse from '@/components/MarkdownCollapse';
    export default {
      components: {
        MarkdownCollapse,
      },
      data() {
        return {
            articles: [
        {
          essayID: '',
          title: '',
          content: '',
          user:{
              userName: '',
              avatar: '',
          },
          releaseTime: '',
          stars:'',
          stared: false
        },
      ],
      userId:'',
      currentPage: 1,
      pageSize: 5,
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
        deletearticle(essayID) {
        this.$axios
          .delete(`/articles/${essayID}`)
          .then((response) => {
            this.articles = this.articles.filter((article) => article.essayID !== essayID);
            this.$message.success('文章删除成功！');
          })
          .catch((error) => {
            console.error('删除文章失败:', error);
            this.$message.error('文章删除失败！');
          });
      },
        toggleStarArticle(articleToUnfollow) {
          this.$axios
    .post('/star-article', articleToUnfollow)
    .then((response) => {
      this.articles = this.articles.filter((article) => article.essayID !==articleToUnfollow.essayID);
      this.$message({
          message: "取消关注成功",
          type: 'success',
          duration: 800,
          });
    })
    .catch((error) => {
      this.$message.error('网络故障！');
    });
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
      },
      created() {
        this.$axios.get('/getuserId')
      .then(response => {
        this.userId=response.data;
      })
      .catch(error => {
        console.error('获取用户信息失败', error);
      });
        this.$axios
          .get('/mystar')
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
          console.error('获取问题列表失败:', error);
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
      border-radius: 10px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    
    .article-avatar-username {
      position: absolute;
      top: 10px;
      left: 10px;
      display: flex;
      align-items: center;
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
    
    .article-content {
      margin-bottom: 10px;
      line-height: 1.5;
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

    
    .article-time {
      font-size: 14px;
      color: #666;
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
    
    .pagination-container {
      text-align: center;
      padding: 20px 0;
      width: 100%;
    }
    .no-results-alert {
      margin-top: 10px;
      width: 100%;
      border-radius: 6px;
      background-color: #f3f4f6;
      color: #666;
      text-align: center;
      padding: 15px;
      font-size: 16px;
    }
    .follow-count {
      font-size: 14px;
      color: #666;
      margin-left: 5px;
    }
    .el-icon-star-on {
      color: #f9a825; 
    }
    .delete-icon {
  color: red;
  font-size: 18px;
  cursor: pointer;
}
    </style>
    