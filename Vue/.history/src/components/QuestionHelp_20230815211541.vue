<template>
  <div class="question-list">
    <el-card
      v-for="question in displayedQuestions"
      :key="question.questionId"
      :class="['question-card', getRandomColor()]"
      @click.stop="navigateToAnswers(question.questionId)"
    >
      <!-- 问题卡片的内容 -->
      <div class="question-avatar-username">
        <div class="question-user-avatar">
          <img :src="question.avatar" alt="User Avatar" class="user-avatar">
        </div>
        <div class="question-username">{{ question.userName }}</div>
      </div>
      <div class="question-details">
        <div class="question-title">{{ question.title }}</div>
        <div class="question-description">{{ question.description }}</div>
        <div class="question-actions">
          <div class="question-actions-left">
            <el-badge :value="question.answers" class="question-answers">
              <el-button type="text" icon="el-icon-edit" class="answer-button" @click.stop="navigateToAnswers(question.questionId)"></el-button>
            </el-badge>
            <span class="question-answers">{{ question.answers }} 回答</span>
          </div>
          <div class="question-actions-right">
            <span class="question-time">发布时间: {{ question.time }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-button type="primary" @click="showQuestionDialog" class="ask-button">我要提问</el-button>

    <el-dialog
      title="提出问题"
      :visible.sync="dialogVisible"
      :close-on-click-modal="false"
      :show-close="ture"
    >
      <el-form ref="questionForm" :model="newQuestion" label-width="80px">
        <el-form-item label="问题标题" required>
          <el-input v-model="newQuestion.title" autocomplete="off" placeholder="请输入问题标题"></el-input>
        </el-form-item>
        <el-form-item label="问题详细描述" required>
          <el-input v-model="newQuestion.description" type="textarea" rows="4" placeholder="请输入问题详细描述"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitQuestion">提交</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="questions.length"
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
      questions: [
        {
          questionId: '',
          title: '',
          description: '',
          userName: '',
          avatar: '',
          time: '',
          answers: '',
        },
        // 其他问题对象...
      ],
      currentPage: 1,
      pageSize: 5,
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
      return this.questions.slice(startIndex, endIndex);
    },
  },
  methods: {
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
      // 构造问题对象
      const questionData = {
        title: this.newQuestion.title,
        description: this.newQuestion.description,
      };
      this.newQuestion.title = '';
      this.newQuestion.description = '';
      // 发送 POST 请求将问题提交到服务器
      this.$axios
        .post('/submit-question', questionData)
        .then((response) => {
          // 提交成功的处理逻辑
          console.log('问题提交成功:', response.data);

          // 刷新问题列表，重新获取问题数据
          this.$axios
            .get('/questions')
            .then((response) => {
              this.questions = response.data.data;
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

          // 给出成功提示
          this.$message(response.data.msg);
        })
        .catch((error) => {
          // 提交失败的处理逻辑
          console.error('问题提交失败:', error);
          this.$message.error('问题提交失败，请重试！');
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
      .get('/questions')
      .then((response) => {
        this.questions = response.data.data;
        // 设置默认头像
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
</style>
