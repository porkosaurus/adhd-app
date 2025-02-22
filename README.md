# 🌱 ADHD Gamified Productivity App

## 🚀 Overview

A vibrant, streak-based app designed for Inattentive ADHD (ADHD-I) users. A unique digital flower grows with completed tasks and wilts when missed, encouraging daily productivity. Features gamification, habit tracking, and ADA compliance.

## 🎯 MVP Features

✅ Gamified To-Do List – Complete tasks, earn rewards, and maintain streaks.

🌿 Growing Digital Garden – Tasks make flowers grow, while inactivity wilts them.

🎁 Digital Rewards – Unlock flowers, achievements, and interactable items.

📅 Habit & Goal Tracking – Multiple gamification mechanics for various habits.

🔔 Smart Notifications – Timely reminders and UI pop-ups.

🎨 Beautiful, Accessible UI – Designed with Material Design principles.

🔄 OS-Level a11y API Integration – Works with screen readers and voice commands.

📱 Target Platforms

Android (Hackathon focus)

Future: Desktop & iOS support

## 🏆 Gamification Mechanics

🌱 Flower Growth – Completing tasks makes flowers grow.

📖 Gardening Scrapbook – Track achievements and flowers collected.

⚡ Streak System – Reward users for consecutive task completion.

💧 Fertilizer & Water System – Enhancements for better habit reinforcement.

📊 Progress Index & Sound Effects – Visual and audio feedback for motivation.

🛠 Assistance for Habit Slumps – Adaptive mechanics to recover lost streaks.

## ♿ ADA Compliance (Android)

Ensuring accessibility for users with ADHD, visual impairments, and motor disabilities.

🔹 Perceivable

✅ High Contrast Mode – Dynamic theming with 4.5:1 contrast.

✅ Screen Reader Support – contentDescription for UI elements.

✅ Scalable Text – Supports font scaling (sp instead of dp).

✅ Alternative Feedback – Vibration + text labels for color-based indicators.

🔹 Operable

✅ Full Keyboard & Switch Access – Supports non-touch navigation.

✅ Large Touch Targets – Minimum 48x48dp.

✅ Haptic Feedback – Vibrations for interactions.

✅ Customizable Animations – "Reduce Motion" setting support.

✅ Voice Command Compatibility – Works with Google Assistant.

🔹 Understandable

✅ Clutter-Free UI – Material Design principles.

✅ Clear Task Instructions – Uses text + icons.

✅ Smart Notifications – Customizable reminders.

🔹 Robust

✅ Tested with Android Accessibility Scanner

✅ Supports Dark Mode & Colorblind-Friendly Palettes

✅ Multi-Device Compatibility – Works on tablets, Chromebooks, foldables.

🚨 Restrictions & Testing

❌ No Small Text – Must support Android font scaling.

❌ No Unlabeled Icons – Actions must have text labels.

❌ No Flashing Animations – Limit to 3 flashes/sec.

❌ No Forced Streak Loss – Users can pause streaks.

✔ Testing Checklist:

Use TalkBack to check screen reader compatibility.

Run Android Accessibility Scanner to detect issues.

Verify Google Assistant voice control.

Check large touch targets (48x48 dp min.).

## ⚡ Tech Stack

Frontend: Jetpack Compose (Kotlin)

Backend: Firebase (Auth, Firestore, Storage)

Tools: Android Studio, Material Design, Accessibility APIs

## 🤝 Contributing

Fork this repo.

Clone your fork:

git clone https://github.com/eanyakpor/team-3-app

Create a new branch:

git checkout -b feature-branch

Make changes & commit:

git commit -m "Added feature"

Push & submit a PR:

git push origin feature-branch

