# 桌面便签

Android窗口小工具实践测试。<br>
在桌面添加便签（通过窗口小工具），可编辑便签内容、字体大小、字体颜色、背景颜色及背景透明度。可通过长按调整便签大小及位置。<br>

## 分支说明
- **main** 分支所使用的布局近乎为单独一个TextView，无法通过RemoteView实现其上下滑动。
- **failed** 分支所使用的布局近乎为单独一个ListView，通过单条目的TextView实现文本可上下滑动。但经深入发现在RemoteViewsService中所持有的RemoteViewsFactory始终为一个，因此当更新数据（如便签内容）时，可能致使多个便签（AppWidget）的内容发生改变。因此对多窗口需要不同的内容的情况不适用。
