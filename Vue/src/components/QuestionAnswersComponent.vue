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
          <div class="answer-info">
            <span class="answer-time">{{ answer.releaseTime }}</span>
            <button
              class="like-button"
              :class="{ liked: answer.liked }"
              @click="toggleLike(answer)"
            >
              👍 {{ answer.likes }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <button class="answer-button" @click="show">回答</button>

    <!-- 分页组件 -->
    <el-pagination
      class="pagination"
      :current-page="currentPage"
      :page-size="pageSize"
      :total="answers.length"
      @current-change="handlePageChange"
    ></el-pagination>

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
          <el-button type="primary" @click="submitAnswer">提交回答</el-button>
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
      answers: [],
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
  methods: {
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
          this.fetchAnswers(); // Fetch updated answers after submitting
        })
        .catch((error) => {
          console.error('Failed to submit answer:', error);
          this.$message.error('回答提交失败');
        });
    },
    toggleLike(answer) {
    if (answer.liked) {
      this.unlikeAnswer(answer);
    } else {
      this.likeAnswer(answer);
    }
  },
  likeAnswer(answer) {
    // Send a request to your server to record the like action
    this.$axios
      .post(`/answers/${answer.answerID}/like`)
      .then((response) => {
        if(response.data.code===500)
        this.$message(response.data.msg);
      else{
        answer.liked = true;
        answer.likes++;
        }
      })
      .catch((error) => {
        console.error('Failed to like answer:', error);
      });
  },
  unlikeAnswer(answer) {
    // Send a request to your server to record the unlike action
    this.$axios
      .post(`/answers/${answer.answerID}/unlike`)
      .then((response) => {
        if(response.data.code===500)
        this.$message(response.data.msg);
      else{
        answer.liked = false;
        answer.likes--;
      }
      })
      .catch((error) => {
        console.error('Failed to unlike answer:', error);
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
</style>