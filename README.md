# ملاحظاتي الاحترافية - Pro Notes 📝✨

> **تطبيق تدوين ملاحظات وقوائم مهام احترافي متكامل مبني بأحدث تقنيات Android و Jetpack Compose و Material Design 3 مع دعم كامل للحفظ المحلي الآمن ومقاومة فقدان البيانات.**
> 
> 👨‍💻 **تصميم وبرمجة: محمد هشام الصلاحي (Mohammed Hisham Al-Salahi)**

[![Developer](https://img.shields.io/badge/Developer-Mohammed%20Hisham%20Al--Salahi-blue.svg)](https://github.com)
[![Android CI](https://github.com/aistudio/pro-notes/actions/workflows/android.yml/badge.svg)](https://github.com/aistudio/pro-notes/actions)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-blue.svg)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-green.svg)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Room-SQLite-orange.svg)](https://developer.android.com/training/data-storage/room)

---

## 🌟 الميزات الرئيسية (Key Features)

### 1. 🛡️ حفظ محلي آمن 100% ومقاومة فقدان البيانات
- **حفظ فوري تلقائي**: حفظ تلقائي لحظي عند كل حرف يُكتب في قاعدة بيانات Room فائقة السرعة.
- **النسخ الاحتياطي والاستعادة المحلية (Local Backup & Restore)**: إنشاء نسخ احتياطية بضغطة زر وحفظها أو استعادتها فورياً.
- **سجل تعديلات الملاحظة (Version History Snapshots)**: تتبع محفوظات التعديلات وإمكانية استرجاع أي إصدار سابق من الملاحظة.
- **سلة محذوفات وأرشيف آمن**: حماية كاملة من الحذف العرضي مع إمكانية الاستعادة الفردية أو الجماعية.

### 2. ✍️ أدوات كتابة وتنسيق غنية
- **شريط تنسيق Markdown**: عناوين (H1, H2, H3)، نصوص عريضة، مائلة، مشطوبة، قوائم نقطية ورقمية، كتل برمجية، واقتباسات.
- **قوالب جاهزة سريعة (Note Templates)**: اجتماعات، يوميات، قوائم تسوق، ملخص كتب، تخطيط مشاريع، ومتابع عادات.
- **قوائم مهام تفاعلية (Interactive Checklists)**: مع شريط تقدم حي ومعدل إنجاز تلقائي.

### 3. 🎨 لوحة رسم يدوي مدمجة (Sketchpad & Canvas)
- رسم يدوي وملاحظات تخطيطية بألوان وأحجام فراشي متعددة مع دعم التراجع والإعادة ومسح اللوحة.
- إدراج الرسمة كعنصر داخل الملاحظة والبطاقات.

### 4. 🎙️ مذكرات صوتية (Voice Memos)
- تسجيل صوتي عالي النقاء مع عداد زمني ومشغل صوتي مدمج داخل الملاحظة.

### 5. 🔒 أمان وقفل الملاحظات برمز PIN
- قفل الملاحظات الحساسة برمز PIN رقمي لحماية الخصوصية.

### 6. 📊 لوحة إحصائيات متقدمة
- تحليل شامل لعدد الملاحظات، معدل إنجاز المهام، عدد الكلمات والحروف، والتوزيع حسب التصنيفات.

---

## 🏗️ البنية البرمجية (Architecture)

- **UI Layer**: Jetpack Compose (Material Design 3), Adaptive Layouts (List / Grid).
- **Architecture Pattern**: MVVM (Model-View-ViewModel) + Repository Pattern.
- **Data Persistence**: Room Database (SQLite), Local JSON Backups, SharedPreferences.
- **Asynchronous / Reactive**: Kotlin Coroutines & StateFlow.
- **Audio & Media**: Android MediaRecorder, MediaPlayer.

---

## 🚀 كيفية البناء والتشغيل (Build & Run Instructions)

### المتطلبات الأساسية
- Android Studio Ladybug / Meerkat أو أحدث.
- JDK 17 أو أحدث.
- Android SDK 34 / 36.

### أوامر Gradle الأساسية
```bash
# بناء نسخة التجربة (Debug APK)
./gradlew assembleDebug

# تشغيل الاختبارات المحلية (Unit & Robolectric Tests)
./gradlew testDebugUnitTest

# فحص الأخطاء والتوافقية
./gradlew check
```

---

## 📂 هيكل المشروع (Project Structure)

```
app/src/main/java/com/example/
├── data/
│   ├── backup/          # BackupManager & Local Data Safety
│   ├── local/           # Room Database & DAO interfaces
│   ├── model/           # Note, ChecklistItem, Drawing, Category models
│   └── repository/      # NoteRepository abstraction
├── ui/
│   ├── components/      # NoteCard, DrawingCanvas, VoiceMemo, RichToolbar, etc.
│   ├── screens/         # HomeScreen, NoteEditor, Archive, Trash, Stats, Backup
│   ├── theme/           # Color, Typography, Theme definition
│   ├── util/            # DateTimeUtils, Markdown & Template helpers
│   └── viewmodel/       # NotesViewModel & UI State Management
└── MainActivity.kt      # Single Activity entry point & Navigation Router
```

---

## 📄 الترخيص (License)
هذا المشروع مفتوح المصدر ومتاح للاستخدام والتطوير.
