<template>
  <div class="question-list">
    <el-card
      v-for="question in displayedQuestions"
      :key="question.questionId"
      :class="['question-card', getRandomColor()]"
      @click.stop="navigateToAnswers(question.questionId)"
    >
      <div class="question-avatar-username">
        <div class="question-user-avatar">
          <img :src="question.avatar" alt="User Avatar" class="user-avatar">
        </div>
        <div class="question-username">{{ question.userName }}</div>
      </div>
      <div class="question-details">
        <div class="question-title">{{ question.title }}</div>
        <div class="question-description">
          <markdown-collapse :content="question.description" />
        </div>
        <div class="question-actions">
          <div class="question-actions-left">
            <el-badge :value="question.answers" >
              <el-button type="text" icon="el-icon-edit" class="answer-button" @click.stop="navigateToAnswers(question.questionId)"></el-button>
            </el-badge>
            <span class="question-answers">{{ question.answers }} 回答</span>
            <el-badge >
              <el-button
                type="text" @click.stop="toggleFollowQuestion(question)"
              >
            <i class="el-icon-star-off" v-if="!question.follower"></i>
            <i class="el-icon-star-on" v-else></i>
              </el-button>
            </el-badge>
            <span class="follow-count">{{ question.followCount }} 关注</span>
          </div>
          <div class="question-actions-right">
            <span class="question-time">发布时间: {{ question.time }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-button type="primary" @click="showQuestionDialog" class="ask-button">我要提问</el-button>
    <template>
  <el-dialog
    title="提出问题"
    :visible.sync="dialogVisible"
    :close-on-click-modal="false"
    :show-close="true"
  >
    <el-form ref="questionForm" :model="newQuestion" label-width="80px">
      <el-form-item label="问题标题" required>
        <el-input v-model="newQuestion.title" autocomplete="off" placeholder="请输入问题标题"></el-input>
      </el-form-item>
      <el-form-item label="问题详细描述" required>
        <el-input v-model="newQuestion.description" type="textarea" rows="4" placeholder="请输入问题详细描述"></el-input>
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
            <el-button type="primary" @click="submitQuestion">提交</el-button>
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
    v-if="filteredQuestions.length !== 0"
    class="pagination-container"
    :current-page="currentPage"
    :page-size="pageSize"
    :total="filteredQuestions.length"
    @current-change="handlePageChange"
  ></el-pagination>
  <el-alert
        v-else
        title="没有找到相关问题"
        type="info"
        show-icon
        class="no-results-alert"
        :closable="false"
      />

  <div class="search-bar">
    <el-input
      v-model="searchKeyword"
      class="search-input"
      placeholder="搜索问题标题"
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
      filteredQuestions: [],
      searchKeyword: '',
      uploadUrl: 'http://localhost:9000/api/upload-image',
      questions: [
        {
          questionId: '',
          title: '',
          description: '',
          userName: '',
          avatar: '',
          time: '',
          answers: '',
          followCount:'',
          follower: false
        },
      ],
      totalDisplayedPages: 0, 
      currentPage: 1,
      pageSize: 5,
      isFollowed:true,
      dialogVisible: false,
      newQuestion: {
        title: '',
        description: '',
      },
    };
  },
  computed: {
    displayedQuestions() {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    const filteredQuestions = this.filteredQuestions.slice(startIndex, endIndex);

    this.totalDisplayedPages = Math.ceil(filteredQuestions.length / this.pageSize);

    return filteredQuestions
  },

  },
  methods: {
    toggleFollowQuestion(question) {
      this.$axios
        .post('/follow-question', question)
        .then((response) => {
            this.$message(response.data.msg)
            if(response.data.code!==500){
              if(response.data.code===200)
                question.followCount++;
              else
                question.followCount--;
              this.$set(question, 'follower', !question.follower);
              this.$forceUpdate()
            }
        }).catch((error)=>{
          this.$message.error('网络故障！');
        })
  },
    handleSearchInput() {
    this.searchQuestions(); 
  },
  searchQuestions() {
    if (this.searchKeyword.trim() === '') {
      this.filteredQuestions=this.questions
      this.currentPage = 1;
    } else {
      this.filteredQuestions = this.questions.filter(question => {
        return question.title.toLowerCase().includes(this.searchKeyword.toLowerCase());
      });
      this.currentPage = 1;
    }
  },
    handleUploadSuccess(response) {
      if (response.data) {
        const imageUrl = response.data;
        this.insertImageIntoQuestionDescription(imageUrl);
        this.$message.success('图片上传成功！');
      } else {
        this.$message.error('图片上传失败，请重试！');
      }
    },
    handleUploadError(file, fileList) {
    this.$message.error('每次最多只能上传3张图片！');
  },
    insertImageIntoQuestionDescription(imageUrl) {
  const imgTag = `<img src="${imageUrl}" style="max-width: 50%; height: auto;" alt="Image">`;
  this.newQuestion.description += `\n${imgTag}\n`;
}
,
    navigateToAnswers(questionId) {
      this.$router.push({ name: 'QuestionAnswers', params: { id: questionId } });
    },
    getRandomColor() {
      const colors = [
        'question-color-1',
        'question-color-2',
        'question-color-3',
        'question-color-4',
        'question-color-5',
      ];
      return colors[Math.floor(Math.random() * colors.length)];
    },
    handlePageChange(currentPage) {
      this.currentPage = currentPage;
    },
    showQuestionDialog() {
      this.dialogVisible = true;
    },
    submitQuestion() {
      if (!this.newQuestion.title.trim()) {
      this.$message.error('问题标题不能为空');
      return;
    }
      const questionData = {
        title: this.newQuestion.title,
        description: this.newQuestion.description,
      };
      this.newQuestion.title = '';
      this.newQuestion.description = '';
      this.$axios
        .post('/submit-question', questionData)
        .then((response) => {
          this.$axios
            .get('/questions')
            .then((response) => {
              this.questions = response.data.data;
              this.filteredQuestions=this.questions;
              this.searchKeyword=''
              this.questions.forEach((question) => {
                if (!question.avatar) {
                  question.avatar =
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
    this.$axios
      .get('/questions')
      .then((response) => {
        this.questions = response.data.data;
        this.filteredQuestions=this.questions
        this.totalDisplayedPages = Math.ceil(this.questions.length / this.pageSize);
        this.questions.forEach((question) => {
          if (!question.avatar) {
            question.avatar =
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
.question-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.question-card {
  margin-bottom: 20px;
  padding: 20px;
  color: black;
  position: relative;
}

.question-avatar-username {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  align-items: flex-start;
}

.question-user-avatar {
  margin-right: 10px;
}

.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.question-username {
  font-size: 14px;
  color: #666;
}

.question-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.question-description {
  margin-bottom: 10px;
}

.question-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-actions-left {
  display: flex;
  align-items: center;
}

.question-answers {
  font-size: 14px;
  color: #666;
  margin-left: 5px;
}
.follow-count {
  font-size: 14px;
  color: #666;
  margin-left: 5px;
}

.question-time {
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

.question-color-1 {
  background-color: #f2f2f2;
}

.question-color-2 {
  background-color: #e6f7ff;
}

.question-color-3 {
  background-color: #f0f9ff;
}

.question-color-4 {
  background-color: #fffbe6;
}

.question-color-5 {
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
</style>
