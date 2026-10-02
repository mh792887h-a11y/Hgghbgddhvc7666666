package com.example.data.model

data class NoteTemplate(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val colorHex: String,
    val isChecklist: Boolean,
    val content: String,
    val checklistItems: List<ChecklistItem> = emptyList()
)

object DefaultTemplates {
    val list = listOf(
        NoteTemplate(
            id = "meeting",
            title = "محضر اجتماع عمل 📋",
            description = "تدوين جدول الأعمال، الحضور، والقرارات المتخذة",
            category = "عمل",
            colorHex = "#E0E7FF",
            isChecklist = false,
            content = """# محضر اجتماع: [عنوان الموضوع]

📅 **التاريخ:** [اليوم والتاريخ]
⏰ **الوقت:** [الساعة]
👥 **الحضور:**
• [الاسم الأول]
• [الاسم الثاني]

---
### 📌 جدول الأعمال والأهداف:
1. مناقشة أهداف الربع الحالي
2. مراجعة المهام المسندة والتحديات

---
### 💡 القرارات والتوصيات:
• القرار الأول: [تفاصيل القرار]
• القرار الثاني: [تفاصيل القرار]

---
### ✅ خطة العمل والمهام القادمة:
- [ ] إرسال التقرير النهائي قبل نهاية الأسبوع
- [ ] جدولة موعد المتابعة القادم"""
        ),
        NoteTemplate(
            id = "goals",
            title = "تخطيط أهداف الأسبوع 🎯",
            description = "قائمة مهام تفاعلية لترتيب أولويات الأسبوع",
            category = "مهام",
            colorHex = "#D1FAE5",
            isChecklist = true,
            content = "",
            checklistItems = listOf(
                ChecklistItem(text = "مراجعة أهم 3 أهداف للأسبوع", isChecked = false),
                ChecklistItem(text = "إنجاز المشروع ذي الأولوية العالية", isChecked = false),
                ChecklistItem(text = "ممارسة الرياضة 3 مرات", isChecked = false),
                ChecklistItem(text = "قراءة 30 دقيقة يومياً", isChecked = false),
                ChecklistItem(text = "تنظيم وترتيب الملفات ومساحة العمل", isChecked = false),
                ChecklistItem(text = "تقييم الإنجاز في نهاية الأسبوع", isChecked = false)
            )
        ),
        NoteTemplate(
            id = "book_summary",
            title = "ملخص كتاب أو مقال 📖",
            description = "توثيق أهم الأفكار والاقتباسات الملهمة",
            category = "دراسة",
            colorHex = "#FEF3C7",
            isChecklist = false,
            content = """# ملخص كتاب: [اسم الكتاب]

✍️ **المؤلف:** [اسم المؤلف]
⭐ **التقييم الشخصي:** ★★★★★
🏷️ **التصنيف:** [تطوير ذات / تقنية / فلسفة...]

---
### 🔑 الفكرة الجوهرية للكتاب:
[اكتب الفكرة الأساسية التي يدور حولها الكتاب باختصار...]

---
### 💡 أهم النقاط والدروس المستفادة:
1. **الدرس الأول:** [شرح موجز]
2. **الدرس الثاني:** [شرح موجز]
3. **الدرس الثالث:** [شرح موجز]

---
### 💬 اقتباسات ملهمة:
> "اكتب هنا اقتباساً أعجبك من الكتاب..."

---
### 🚀 كيف سأطبق هذه المعرفة في حياتي؟
• تطبيق الخطوة الأولى فورياً في روتيني اليومي."""
        ),
        NoteTemplate(
            id = "project_plan",
            title = "تخطيط مشروع جديد 🚀",
            description = "هيكلة فكرة مشروع من البداية حتى التنفيذ",
            category = "أفكار",
            colorHex = "#CFFAFE",
            isChecklist = false,
            content = """# خطة مشروع: [اسم المشروع]

🎯 **الهدف الأساسي:** [ما هي المشكلة التي يحلها المشروع؟]
👥 **الجمهور المستهدف:** [الفئة المستهدفة]

---
### 🛠️ المتطلبات التقنية والموارد:
• التقنيات المستخدمة: Kotlin / Jetpack Compose
• الأدوات والمكتبات المطلوبة

---
### 📅 المراحل الزمنية (Milestones):
1. **المرحلة 1:** تصميم الواجهات وتجربة المستخدم
2. **المرحلة 2:** بناء قاعدة البيانات والمنطق البرمجي
3. **المرحلة 3:** الاختبار وإصلاح الأخطاء
4. **المرحلة 4:** الإطلاق والتوزيع

---
### ⚠️ المخاطر المحتملة والحلول:
• التحدي الأول -> الحل البديل المقترح"""
        ),
        NoteTemplate(
            id = "grocery",
            title = "قائمة مشتريات وتنظيم المنزل 🛒",
            description = "قائمة مهام لمستلزمات البيت والتسوق",
            category = "شخصي",
            colorHex = "#FCE7F3",
            isChecklist = true,
            content = "",
            checklistItems = listOf(
                ChecklistItem(text = "خضار وفواكه طازجة", isChecked = false),
                ChecklistItem(text = "حليب ومشتقات الألبان", isChecked = false),
                ChecklistItem(text = "خبز ومخبوزات", isChecked = false),
                ChecklistItem(text = "مستلزمات النظافة المنزلية", isChecked = false),
                ChecklistItem(text = "مشروبات وشاي وقهوة", isChecked = false)
            )
        ),
        NoteTemplate(
            id = "diary",
            title = "مذكرات ويوميات شخصية 📔",
            description = "تدوين مشاعر اليوم، الإنجازات، والامتنان",
            category = "شخصي",
            colorHex = "#F3E8FF",
            isChecklist = false,
            content = """# يوميات: [تاريخ اليوم]

✨ **مزاج اليوم:** ممتن وسعيد 😊
⛅ **حالة الطقس:** مشمس وجميل

---
### 🙏 3 أشياء أنا ممتن لها اليوم:
1. [الشيء الأول]
2. [الشيء الثاني]
3. [الشيء الثالث]

---
### 🌟 أبرز حدث ومشاعر اليوم:
[اكتب تفاصيل يومك ومشاعرك بكل راحة...]

---
### 💡 فكرة أو درس تعلمته اليوم:
[ما الذي يمكنني تحسينه غداً؟]"""
        )
    )
}
