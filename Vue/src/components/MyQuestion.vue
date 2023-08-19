<template>
  <div class="question-list">
    <div
      v-for="question in displayedQuestions"
      :key="question.questionId"
      :class="['question-card', getRandomColor()]"
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
            <el-button type="text" icon="el-icon-edit" class="answer-button" @click.stop="navigateToAnswers(question.questionId)"></el-button>
            <span class="question-answers">{{ question.answers }} 回答</span>
            <el-badge>
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
            <i class="el-icon-delete delete-icon" @click.stop="deleteQuestion(question.questionId)"></i>
          </div>
        </div>
      </div>
  </div>
  <div class="pagination-space"></div>
  <el-pagination
    v-if="questions.length !== 0"
    class="pagination-container"
    :current-page="currentPage"
    :page-size="pageSize"
    :total="questions.length"
    @current-change="handlePageChange"
  ></el-pagination>
  <el-alert
        v-else
        title="您还没提过问"
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
      this.$axios
        .delete(`/questions/${questionId}`)
        .then((response) => {
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
    this.$axios
      .get('/myquestions')
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
</style>
