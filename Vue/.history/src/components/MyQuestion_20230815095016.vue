<template>
  <div class="question-list">
    <div
      v-for="question in displayedQuestions"
      :key="question.questionId"
      :class="['question-card', getRandomColor()]"
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
            <el-button type="text" icon="el-icon-edit" class="answer-button" @click.stop="navigateToAnswers(question.questionId)"></el-button>
            <span class="question-answers">{{ question.answers }} 回答</span>
          </div>
          <div class="question-actions-right">
            <span class="question-time">发布时间: {{ question.time }}</span>
            <i class="el-icon-delete delete-icon" @click.stop="deleteQuestion(question.questionId)"></i>
          </div>
        </div>
      </div>
    </div>

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
export default {
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
    deleteQuestion(questionId) {
      // 发送删除问题的请求，并更新列表
      this.$axios
        .delete(`/questions/${questionId}`)
        .then((response) => {
          // 根据返回的结果进行处理，比如更新列表或显示提示信息
          this.questions = this.questions.filter((question) => question.questionId !== questionId);
          this.$message.success('问题删除成功！');
        })
        .catch((error) => {
          console.error('删除问题失败:', error);
          this.$message.error('问题删除失败！');
        });
    },
  },
  created() {
    // 组件加载时发送请求获取问题列表
    this.$axios
      .get('/myquestions')
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
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.question-avatar-username {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  align-items: center;
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
  line-height: 1.5;
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

.answer-button {
  font-size: 18px !important;
}

.delete-icon {
  color: red;
  font-size: 18px;
  cursor: pointer;
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

/* 移除小红点样式 */
.el-badge .el-badge__content {
  display: none;
}
</style>
