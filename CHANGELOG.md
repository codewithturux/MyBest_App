# Changelog

All notable changes to the MyBest UBSI app will be documented in this file.

## [1.1.0] - 2025-12-11

### Dashboard
- ✅ Replaced statistics cards with **App Status** card (Login Status, Web Connected)
- ✅ Added dynamic "Kelas Hari Ini" section with compact schedule cards
- ✅ Added **SwipeRefreshLayout** for pull-to-refresh functionality
- ✅ Created new `item_dashboard_schedule.xml` - compact card for dashboard

### Jadwal Kuliah
- ✅ Fixed **double app bar** issue by removing fragment's custom app bar
- ✅ Redesigned stats cards: 2 compact side-by-side cards (Mata Kuliah + Total SKS)
- ✅ Enhanced stats labels with bold + primary color
- ✅ Added SwipeRefreshLayout for pull-to-refresh
- ✅ **Kuliah Mendatang** card now navigates to Presensi page on click
- ✅ Improved countdown format: "X hari Y jam", "X jam Y menit", etc.

### Schedule Card (`item_schedule_card.xml`)
- ✅ Complete redesign: colored header at top, compact 2-column info grid
- ✅ All info labels (Dosen, SKS, Kode, Ruang, etc.) now bold + primary color
- ✅ Smaller icons (14dp) and font sizes (11sp) for compact look
- ✅ Added new icons: `ic_group.xml`, `ic_link.xml`

### Profil
- ✅ Made layout more compact:
  - Avatar: 80dp → 56dp
  - Padding: 20dp → 16dp
  - Icons: 32dp → 24dp
  - Button height: 56dp → 48dp

### Sidebar Navigation
- ✅ Added **active state highlight** for current page
- ✅ Created `drawer_item_background.xml` - background selector
- ✅ Created `drawer_icon_tint.xml` - icon color selector
- ✅ Created `drawer_text_color.xml` - text color selector

### Presensi Activity
- ✅ Added SwipeRefreshLayout wrapper

### Dependencies
- ✅ Added `androidx.swiperefreshlayout:swiperefreshlayout:1.1.0`

### New Resources
- `ic_wifi.xml` - WiFi icon for web status
- `ic_group.xml` - Group icon for Kel Praktek
- `ic_link.xml` - Link icon for Kode Gabung
- `item_dashboard_schedule.xml` - Compact schedule card for dashboard
- `drawer_item_background.xml` - Drawer item selector
- `bg_drawer_item_checked.xml` - Checked state background
- `drawer_icon_tint.xml` - Icon tint selector
- `drawer_text_color.xml` - Text color selector

### Strings Added
- `app_status`, `login_status`, `web_connected`
- `valid`, `invalid`, `yes`, `no`
- `today_classes`, `no_classes_today`

---

## [1.0.0] - Initial Release

- Basic app structure with Dashboard, Jadwal, Profil
- Guest mode login
- Schedule display with presensi tracking
- Notification system
- About page
