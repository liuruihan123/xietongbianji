import VueI18n from '@intlify/unplugin-vue-i18n/vite';

export default (path: any) => {
  return VueI18n({
    // 设置语言文件的目录
    include: [path.resolve(path.resolve(__dirname, '../../src'), 'lang/**')],
    // 设置为 composition API 模式
    compositionOnly: true,
    // 设置为运行时编译
    runtimeOnly: false
  });
};
