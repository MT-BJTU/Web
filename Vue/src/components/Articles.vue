<template>
  <div class="article-list">
    <el-card
      v-for="article in displayedArticles"
      :key="article.ArticleId"
      :class="['article-card', getRandomColor()]"
      @click.stop="navigateToAnswers(article.ArticleId)"
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
            <el-badge >
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
    </el-card>

    <el-button type="primary" @click="showArticleDialog" class="write-button">随笔</el-button>
    <template>
  <el-dialog
    title="提出文章"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :show-close="true"
  >
    <el-form ref="ArticleForm" :model="newArticle" label-width="80px">
      <el-form-item label="文章标题" required>
        <el-input v-model="newArticle.title" autocomplete="off" placeholder="请输入文章标题"></el-input>
      </el-form-item>
      <el-form-item label="文章内容" required>
        <el-input v-model="newArticle.content" type="textarea" rows="4" placeholder="请输入文章内容"></el-input>
      </el-form-item>
      <el-form-item>
        <el-upload
  class="upload-demo"
  ref="upload"
  :action="uploadUrl"
  :on-success="handleUploadSuccess"
  :on-error="handleUploadError"
  :show-file-list="false"
>
  <el-button size="small" type="primary">
    <i class="el-icon-upload"></i> 点击上传图片
  </el-button>
</el-upload>
      </el-form-item>
      <el-form-item>
        <el-row>
          <el-col :span="12">
            <el-button type="primary" @click="submitArticle">提交</el-button>
          </el-col>
          <el-col :span="12" class="text-right">
            <el-button @click="dialogVisible = false">取消</el-button>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<el-pagination
    v-if="filteredArticles.length !== 0"
    class="pagination-container"
    :current-page="currentPage"
    :page-size="pageSize"
    :total="filteredArticles.length"
    @current-change="handlePageChange"
  ></el-pagination>
  <el-alert
        v-else
        title="没有找到相关文章"
        type="info"
        show-icon
        class="no-results-alert"
        :closable="false"
      />

  <div class="search-bar">
    <el-input
      v-model="searchKeyword"
      class="search-input"
      placeholder="搜索文章标题"
      clearable
      @input="handleSearchInput"
      prefix-icon="el-icon-search"
    ></el-input>
  </div>
  </div>
</template>

<script>
import MarkdownCollapse from '@/components/MarkdownCollapse';
import { ElBadge } from 'element-ui';
export default {
  components: {
    MarkdownCollapse,
    'el-badge': ElBadge,
  },
  data() {
    return {
      filteredArticles: [],
      searchKeyword: '',
      uploadUrl: 'http://localhost:9000/api/upload-image',
      Articles: [
        {
          essayId: '',
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
      totalDisplayedPages: 0, 
      currentPage: 1,
      pageSize: 5,
      isStared:true,
      dialogVisible: false,
      newArticle: {
        title: '',
        content: '',
      },
    };
  },
  computed: {
    displayedArticles() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    const filteredArticles = this.filteredArticles.slice(startIndex, endIndex);

    this.totalDisplayedPages = Math.ceil(filteredArticles.length / this.pageSize);

    return filteredArticles
  },

  },
  methods: {
    deletearticle(essayID) {
        this.$axios
          .delete(`/articles/${essayID}`)
          .then((response) => {
            this.Articles = this.Articles.filter((article) => article.essayID !== essayID);
            this.filteredArticles=this.filteredArticles.filter((article) => article.essayID !== essayID);
            this.$message.success('文章删除成功！');
          })
          .catch((error) => {
            console.error('删除文章失败:', error);
            this.$message.error('文章删除失败！');
          });
      },
    toggleStarArticle(article) {
      this.$axios
        .post('/star-article', article)
        .then((response) => {
          this.$message({
          message: response.data.msg,
          type: 'success',
          duration: 800,
          });
            if(response.data.code!==500){
              if(response.data.code===200)
                article.stars++;
              else
                article.stars--;
              this.$set(article, 'stared', !article.stared);
              this.$forceUpdate()
            }
        }).catch((error)=>{
          this.$message.error('网络故障！');
        })
  },
    handleSearchInput() {
    this.searchArticles(); 
  },
  searchArticles() {
    if (this.searchKeyword.trim() === '') {
      this.filteredArticles=this.Articles
      this.currentPage = 1;
    } else {
      this.filteredArticles = this.Articles.filter(article => {
        return article.title.toLowerCase().includes(this.searchKeyword.toLowerCase());
      });
      this.currentPage = 1;
    }
  },
    handleUploadSuccess(response) {
      if (response.data) {
        const imageUrl = response.data;
        this.insertImageIntoArticlecontent(imageUrl);
        this.$message.success('图片上传成功！');
      } else {
        this.$message.error('图片上传失败，请重试！');
      }
    },
    handleUploadError(file, fileList) {
    this.$message.error('每次最多只能上传3张图片！');
  },
    insertImageIntoArticlecontent(imageUrl) {
  const imgTag = `<img src="${imageUrl}" style="max-width: 50%; height: auto;" alt="Image">`;
  this.newArticle.content += `\n${imgTag}\n`;
}
,
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
    showArticleDialog() {
      this.dialogVisible = true;
    },
    submitArticle() {
      if (!this.newArticle.title.trim()) {
      this.$message.error('标题不能为空');
      return;
    }
      const ArticleData = {
        title: this.newArticle.title,
        content: this.newArticle.content,
      };
      this.newArticle.title = '';
      this.newArticle.content = '';
      this.$axios
        .post('/submit-article', ArticleData)
        .then((response) => {
          this.$axios
            .get('/articles')
            .then((response) => {
              this.Articles = response.data.data;
              this.filteredArticles=this.Articles;
              this.searchKeyword=''
              this.Articles.forEach((article) => {
                if (!article.user.avatar) {
                  article.user.avatar =
                    'https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png';
                }
              });
            })
            .catch((error) => {
              console.error('获取问题列表失败:', error);
            });
          this.$message(response.data.msg);
        })
        .catch((error) => {
          console.error('问题提交失败:', error);
          this.$message.error('问题提交失败，请重试！');
        })
        .finally(() => {
          this.dialogVisible = false;
        });
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
      .get('/articles')
      .then((response) => {
        this.Articles = response.data.data;
        this.filteredArticles=this.Articles
        this.totalDisplayedPages = Math.ceil(this.Articles.length / this.pageSize);
        this.Articles.forEach((article) => {
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

.article-content {
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
.star-count {
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

.write-button {
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
.search-bar {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 999;
  display: flex;
  align-items: center;
  background-color: #fff; /* 修改背景颜色 */
  border: 1px solid #ddd; /* 添加边框 */
  border-radius: 25px;
  padding: 0 15px;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.1);
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


.el-icon-star-on {
  color: #f9a825; 
}

.delete-icon {
  color: red;
  font-size: 18px;
  cursor: pointer;
}
</style>