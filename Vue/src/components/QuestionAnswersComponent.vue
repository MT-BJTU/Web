<template>
  <div class="question-answers-page">
    <div class="question-details">
      <div class="question-user-info">
        <img :src="question.user.avatar" alt="User Avatar" class="user-avatar">
        <span class="question-username">{{ question.user.userName }}</span>
      </div>
      <h2 class="question-title">{{ question.title }}</h2>
      <div class="question-info">
        <span class="question-time">{{ question.time }}</span>
      </div>
      <markdown-collapse :content="question.description" />
    </div>

    <div class="answers-list">
      <div v-for="(answer, index) in paginatedAnswers" :key="answer.answerID" class="answer-card" :style="getCardStyle(index)">
        <div class="answer-user-info">
          <img :src="answer.user.avatar" alt="User Avatar" class="user-avatar">
          <span class="answer-username">{{ answer.user.userName }}</span>
        </div>
        <div class="answer-content">
          <markdown-collapse :content="answer.content" />
        </div>
        <div class="answer-actions">
          <div class="answer-actions-left">
            <el-badge :value="answer.likes" class="like-badge">
                <el-button
                  type="text"
                  @click="likeAnswer(answer)"
                >
                 <p v-if="answer.liked">点赞</p>
                 <p v-else>不点赞</p>
                </el-button>
              </el-badge>
          </div> 
          <div class="answer-actions-right">
            <span class="answer-time">发布时间: {{ answer.releaseTime }}</span>
            <i v-if="answer.userID===userId"  class="el-icon-delete delete-icon" @click.stop="deleteAnswer(answer.answerID)"></i>
          </div>
        </div>
      </div>
    </div>

    <button class="answer-button" @click="show">回答</button>
    <el-pagination
    v-if="answers.length !== 0"
    class="pagination-container"
    :current-page="currentPage"
      :page-size="pageSize"
      :total="answers.length"
      @current-change="handlePageChange"
  ></el-pagination>
  <el-alert
        v-else
        title="还没有人回答"
        type="info"
        show-icon
        class="no-results-alert"
        :closable="false"
      />
    <el-dialog
      title="回答问题"
      :visible.sync="showAnswerForm"
      width="30%"
      @close="resetAnswerForm"
    >
    <el-form ref="answerForm" :model="newAnswer" label-width="80px">
    <el-form-item label="回答内容" required>
      <el-input
        v-model="newAnswer.content"
        type="textarea"
        rows="4"
        placeholder="请输入回答内容"
      ></el-input>
    </el-form-item>

    <el-form-item>
      <div class="button-container">
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
        <el-button type="primary" @click="submitAnswer">提交回答</el-button>
      </div>
    </el-form-item>
  </el-form>
    </el-dialog>
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
      uploadUrl: 'http://localhost:9000/api/upload-image',
      question: {
        id: null,
        title: '',
        user: {
          userName: '',
          avatar: 'https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png'
        },
        time: '',
        description: '',
      },
      answers: [
        {
          like:'',
          liked:false
        }
      ],
      userId:'',
      showAnswerForm: false,
      newAnswer: {
        content: ''
      },
      currentPage: 1,
      pageSize: 10,
    };
  },
  computed: {
    paginatedAnswers() {
      const startIndex = (this.currentPage - 1) * this.pageSize;
      const endIndex = startIndex + this.pageSize;
      return this.answers.slice(startIndex, endIndex);
    },
  },
  mounted() {
    this.fetchQuestion();
    this.fetchAnswers();
  },
  created() {
    this.$axios.get('/getuserId')
      .then(response => {
        this.userId=response.data;
      })
      .catch(error => {
        console.error('获取用户信息失败', error);
      });
  },
  methods: {
    deleteAnswer(answerID) {
      this.$axios
        .delete(`/answers/${answerID}`)
        .then((response) => {
          this.answers = this.answers.filter((answer) => answer.answerID !== answerID);
          this.$message.success('回答删除成功！');
        })
        .catch((error) => {
          console.error('回答问题失败:', error);
          this.$message.error('回答删除失败！');
        });
    },
    handleUploadSuccess(response) {
      if (response.data) {
        const imageUrl = response.data; // 从后端返回的图片URL
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
  // 插入图片并设置样式
  const imgTag = `<img src="${imageUrl}" style="max-width: 50%; height: auto;" alt="Image">`;
  this.newAnswer.content += `\n${imgTag}\n`;
}
,
    fetchQuestion() 
    {
      const questionId = this.$route.params.id;
      this.$axios
        .get(`/questions/${questionId}`)
        .then((response) => {
          this.question = response.data.data;
          if (!this.question.user.avatar) {
            this.question.user.avatar = 'https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png';
          }
        })
        .catch((error) => {
          console.error('Failed to fetch question:', error);
        });
    },
    fetchAnswers() {
      const questionId = this.$route.params.id;
      this.$axios
        .get(`/questions/${questionId}/answers`)
        .then((response) => {
          this.answers = response.data.data;
          this.answers.forEach((answer) => {
            if (!answer.user.avatar) {
              answer.user.avatar = 'https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041932702.png';
            }
          });
        })
        .catch((error) => {
          console.error('Failed to fetch answers:', error);
        });
    },
    show() {
      this.showAnswerForm = true;
    },
    resetAnswerForm() {
      this.newAnswer.content = '';
    },
    handlePageChange(currentPage) {
      this.currentPage = currentPage;
    },
    getCardStyle(index) {
      const colors = [
        '#ffe5d9',
        '#e8f0fe',
        '#fdeaf4',
        '#eaf5fe',
        '#f3fee1',
        '#fce4e7',
        '#f2f8ed',
        '#fffef3',
        '#e9f1f3',
        '#fff3db'
      ];
      const randomColor = colors[index % colors.length];
      return { backgroundColor: randomColor };
    },
    submitAnswer() {
      const questionId = this.$route.params.id;
      const Answer = {
        content: this.newAnswer.content,
        releaseTime: new Date().toLocaleString(),
      };
      this.$axios
        .post(`questions/${questionId}/postanswers`, Answer)
        .then((response) => {
          this.showAnswerForm = false;
          this.$message(response.data.msg);
          this.fetchAnswers(); 
        })
        .catch((error) => {
          console.error('Failed to submit answer:', error);
          this.$message.error('回答提交失败');
        });
    },
    likeAnswer(answer) {
      this.$axios
        .post(`/answers/${answer.answerID}/like`)
        .then((response) => {
          this.$message({
          message: response.data.msg,
          type: 'success',
          duration: 800,
          });
          if(response.data.code!==500){
          if (response.data.code===200) {
            answer.likes++;
          } else if(response.data.code===201){
            answer.likes--;
          }
            this.$set(answer, 'liked', !answer.liked);
            this.$forceUpdate()
        }
        })
        .catch((error) => {
          console.error('Failed to like answer:', error);
        });
    },
  },
};
</script>

<style scoped>
.question-answers-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.question-details {
  margin-bottom: 20px;
}

.question-user-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 10px;
}

.question-username {
  font-size: 16px;
  font-weight: bold;
}

.question-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.question-info {
  color: #999;
  font-size: 14px;
  margin-bottom: 10px;
}

.question-description {
  margin-bottom: 20px;
}

.answers-list {
  margin-bottom: 20px;
}

.answer-card {
  border: 1px solid #eee;
  padding: 20px;
  margin-bottom: 20px;
}

.answer-user-info {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.answer-username {
  font-size: 16px;
  font-weight: bold;
}

.answer-content p {
  margin-bottom: 10px;
}

.answer-info {
  color: #999;
  font-size: 14px;
}

.answer-button {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 10px 20px;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
}
.like-button {
  margin-top: 10px;
  display: inline-block;
  cursor: pointer;
  background-color: #007bff;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 5px 10px;
}

.pagination-container {
  text-align: center;
  padding: 20px 0;
  width: 100%;
}

.fixed-bottom {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 999;
  margin-right: 300px; 
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
.button-container {
  display: flex;
  justify-content: space-between;
}

.liked {
  color: red;
}
.answer-time {
  font-size: 14px;
  color: #666;
}
.delete-icon {
  color: red;
  font-size: 18px;
  cursor: pointer;
}
.answer-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.answer-actions-left {
  display: flex;
  align-items: center;
}
</style>