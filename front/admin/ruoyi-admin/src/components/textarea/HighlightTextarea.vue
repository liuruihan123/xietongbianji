<template>
  <div class="editor-container">
    <div class="editor">
      <div class="toolbar">
         <el-button type="primary" size="small" @click="insertFragment1">插入片段1</el-button>
        <el-button type="primary" size="small" @click="insertFragment2">插入片段2</el-button>
        <el-button type="primary" size="small" @click="insertResultContent">插入一段分析结果</el-button>
        <el-button type="primary" size="small" @click="inserttotalContent">插入全文</el-button>
        <!-- <el-button type="warning" size="small" @click="insertCaseVar">插入案例库引用</el-button> -->
        <!-- <el-button type="primary" size="small" @click="insertKnowledgeVar">插入知识库检索结果变量</el-button>
        <el-button type="success" size="small" @click="insertQuestionVar">插入用户问题变量</el-button>
        <el-button type="warning" size="small" @click="insertCaseVar">插入案例库引用</el-button> -->
      </div>
      <quill-editor
        ref="myQuillEditor"
        v-model:content="content"
        contentType="html"
        @textChange="handleTextChange"
        @focus="handleFocus"
        @compositionstart="handleCompositionStart"
        @compositionend="handleCompositionEnd"
        :options="options"
        :style="styles"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { QuillEditor, Quill } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import { ref, computed, watch, nextTick } from 'vue';

// 添加 isProcessing 变量定义
let isProcessing = false;

// 添加输入法编辑标志
let isComposing = false;

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  height: {
    type: Number,
    default: 200
  }
});

const emit = defineEmits(['update:modelValue']);
const myQuillEditor = ref();
const content = ref('');

// 自定义变量格式
const Variable = Quill.import('formats/color');
Quill.register(Variable, true);

// 编辑器配置
const options = {
  theme: 'snow',
  modules: {
    toolbar: false // 禁用默认工具栏
  },
  placeholder: '请输入内容'
};

// 编辑器样式
const styles = computed(() => ({
  height: `${props.height}px`,
  width: '100%'
}));

// 添加处理焦点的函数
const handleFocus = () => {
  const quill = myQuillEditor.value.getQuill();
  quill.format('color', false);
  quill.format('bold', false);
};

// // 插入知识库变量
// const insertKnowledgeVar = () => {
//   insertVariable('{{data}}');
// };

// // 插入问题变量
// const insertQuestionVar = () => {
//   insertVariable('{{question}}');
// };

// // 插入案例变量
// const insertCaseVar = () => {
//   insertVariable('{{case}}');
// };

//插入片段
const insertFragment1 = () => {
  insertVariable('{{fragment1}}');
};
const insertFragment2 = () => {
  insertVariable('{{fragment2}}');
};

const insertResultContent = () => {
  insertVariable('{{resultContent}}');
};
const inserttotalContent = () => {
  insertVariable('{{totalContent}}');
};

// 统一的插入变量函数
const insertVariable = (varText: string) => {
  const quill = myQuillEditor.value?.getQuill();
  if (!quill) return;

  const range = quill.getSelection(true);
  if (range) {
    const currentIndex = range.index;

    // 插入变量
    quill.insertText(currentIndex, varText, {
      'color': '#409EFF',
      'bold': true
    });

    // 移动光标到变量后面
    quill.setSelection(currentIndex + varText.length, 0);

    // 重置格式，这样后续输入不会继承变量的样式
    quill.format('color', false);
    quill.format('bold', false);
  }
};

// 统一的内容更新函数
const updateContent = (text: string) => {
  if (isProcessing) return;
  isProcessing = true;

  try {
    // 处理换行符
    const processedContent = text.replace(/\n/g, '\\n');
    content.value = processedContent;
    emit('update:modelValue', processedContent);
  } finally {
    isProcessing = false;
  }
};

// 添加输入法事件处理函数
const handleCompositionStart = () => {
  isComposing = true;
};

const handleCompositionEnd = () => {
  isComposing = false;
  // 在输入法结束后触发一次文本更新
  handleTextChange();
};

// 文本变化处理
const handleTextChange = () => {
  // 如果正在输入法编辑中，不处理文本更新
  if (isProcessing || isComposing) return;

  const quill = myQuillEditor.value?.getQuill();
  if (!quill) return;

  isProcessing = true;
  try {
    const currentSelection = quill.getSelection();
    const text = quill.getText();

    emit('update:modelValue', text.replace(/\n/g, '\\n'));

    // 高亮变量处理...
    const variables = ['{{fragment1}}', '{{fragment2}}', '{{resultContent}}', '{{totalContent}}'];
    variables.forEach(varText => {
      const regex = new RegExp(varText.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g');
      let match;
      while ((match = regex.exec(text)) !== null) {
        quill.formatText(match.index, varText.length, {
          'color': '#409EFF',
          'bold': true
        });
      }
    });

    if (currentSelection) {
      quill.setSelection(currentSelection);
    }
  } finally {
    isProcessing = false;
  }
};

// 高亮变量
const highlightVariables = (text: string) => {
  const quill = myQuillEditor.value?.getQuill();
  if (!quill) return;

  // 处理换行符并设置文本
  const processedText = text.replace(/\\n/g, '\n');
  quill.setText(processedText);

  // 高亮所有变量
  const regex = /\{\{data\}\}/g;
  let match;
  while ((match = regex.exec(processedText)) !== null) {
    quill.formatText(match.index, 8, {
      'color': '#409EFF',
      'bold': true
    });
  }
};

// 监听值变化
watch(() => props.modelValue, (newVal) => {
  if (isProcessing) return;
  isProcessing = true;

  try {
    const quill = myQuillEditor.value?.getQuill();
    if (!quill) return;

    if (!newVal) {
      content.value = '';
      quill.setText('');
      return;
    }

    // 保存当前光标位置
    const currentSelection = quill.getSelection();

    // 处理文本
    const processedValue = newVal.replace(/\\n/g, '\n');
    quill.setText(processedValue);

    // 高亮所有变量（添加 case 变量）
    const variables = ['{{fragment1}}', '{{fragment2}}', '{{resultContent}}', '{{totalContent}}'];
    variables.forEach(varText => {
      const regex = new RegExp(varText.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g');
      let match;
      while ((match = regex.exec(processedValue)) !== null) {
        quill.formatText(match.index, varText.length, {
          'color': '#409EFF',
          'bold': true
        });
      }
    });

    // 恢复光标位置
    if (currentSelection) {
      quill.setSelection(currentSelection);
    }
  } finally {
    isProcessing = false;
  }
}, { immediate: true });

// 清空方法
const clear = () => {
  content.value = '';
  if (myQuillEditor.value) {
    const quill = myQuillEditor.value.getQuill();
    quill?.setText('');
  }
};

// 如果需要，可以暴露给父组件
defineExpose({
  clear
});
</script>

<style scoped>
.editor-container {
  width: 100%;
}

.editor {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  width: 100%;
}

.toolbar {
  padding: 8px;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  gap: 8px;  /* 添加按钮之间的间距 */
}

:deep(.ql-container) {
  font-size: 14px;
  line-height: 1.5;
  width: 100%;
}

:deep(.ql-editor) {
  min-height: 200px;
  width: 100%;
}
</style>
