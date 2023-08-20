<template>
  <div class="custom-expand">
    <div class="content-container">
      <vue-markdown :source="renderedContent" class="markdown-content"></vue-markdown>
      <div v-if="shouldShowExpandButton()" class="expand-link" @click="toggleExpanded">
        <el-icon :class="expanded ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></el-icon>
      </div>
    </div>
  </div>
</template>

<script>
import { ElIcon } from 'element-ui';
import VueMarkdown from 'vue-markdown';

export default {
  components: {
    ElIcon,
    VueMarkdown,
  },
  props: {
    content: String,
  },
  data() {
    return {
      expanded: false,
      show:''
    };
  },
  computed: {
    renderedContent() {
      if (this.expanded) {
        return this.content;
      } else {
        this.show=true
        const maxTextLength = 100; 
        let textToDisplay = this.content; 
        let length=this.content.length
        if (textToDisplay.length > maxTextLength) {
          const lastImageTagIndex = this.content.lastIndexOf('<img', maxTextLength); 
          if (lastImageTagIndex !== -1) {
            const firstIndex=this.content.indexOf('>',maxTextLength+1);  
            if(firstIndex+2===length){
              this.show=false;
              return this.content
            }
            textToDisplay = this.content.substring(0, firstIndex); 
          } else {
            textToDisplay = textToDisplay.substr(0, maxTextLength); 
          }
          return textToDisplay + '...';
        } else {
          return textToDisplay;
        }
      }
    },
  },
  methods: {
    shouldShowExpandButton() {
      return this.content.length > 100&&this.show;
    },
    toggleExpanded() {
      this.expanded = !this.expanded;
    },
  },
};
</script>

<style scoped>
.custom-expand {
  position: relative;
}

.content-container {
  position: relative;
  overflow: hidden; /* 添加溢出隐藏属性 */
}

.expand-link {
  position: absolute;
  bottom: 0;
  right: 0;
  display: flex;
  align-items: center;
  cursor: pointer;
  color: blue;
}

.markdown-content {
  max-width: 100%;
  overflow-wrap: break-word;
  display: inline-block;
}

</style>
