package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AuthStage
import com.example.ui.RedtextViewModel
import com.example.ui.VaultTab
import com.example.ui.components.TopSecurityBar
import com.example.ui.screens.CloudDataScreen
import com.example.ui.screens.DeviceVerificationScreen
import com.example.ui.screens.DevicesManagerScreen
import com.example.ui.screens.OwnerCloudConsoleScreen
import com.example.ui.screens.PinLockScreen
import com.example.ui.screens.SecureLoginOtpScreen
import com.example.ui.screens.VaultHomeScreen
import com.example.ui.screens.WebBrowserPortalScreen
import com.example.ui.theme.BlackCardBorder
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonRedLight
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.RedtextTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

class MainActivity : ComponentActivity() {

    private val viewModel: RedtextViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedtextTheme {
                RedtextApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun RedtextApp(viewModel: RedtextViewModel) {
    val authState by viewModel.authUiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val items by viewModel.allItems.collectAsStateWithLifecycle()
    val devices by viewModel.allDevices.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val cloudFiles by viewModel.allCloudFiles.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val revealedItemIds by viewModel.revealedItemIds.collectAsStateWithLifecycle()

    // BackHandler for safe navigation
    BackHandler(enabled = authState.stage == AuthStage.AUTHENTICATED && selectedTab != VaultTab.CLOUD_FILES) {
        viewModel.selectTab(VaultTab.CLOUD_FILES)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack),
        containerColor = PitchBlack,
        topBar = {
            if (authState.stage == AuthStage.AUTHENTICATED || authState.stage == AuthStage.PIN_LOCK) {
                TopSecurityBar(
                    isLocked = authState.stage == AuthStage.PIN_LOCK,
                    onLockClicked = { viewModel.lockVault() },
                    modifier = Modifier.statusBarsPadding()
                )
            }
        },
        bottomBar = {
            if (authState.stage == AuthStage.AUTHENTICATED) {
                NavigationBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .border(1.dp, BlackCardBorder)
                        .testTag("bottom_nav_bar"),
                    containerColor = PitchBlack,
                    contentColor = TextWhite
                ) {
                    NavigationBarItem(
                        selected = selectedTab == VaultTab.CLOUD_FILES,
                        onClick = { viewModel.selectTab(VaultTab.CLOUD_FILES) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Cloud",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Cloud",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == VaultTab.CLOUD_FILES) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonRed,
                            selectedTextColor = CrimsonRedLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CrimsonContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_cloud")
                    )

                    NavigationBarItem(
                        selected = selectedTab == VaultTab.OWNER_PANEL,
                        onClick = { viewModel.selectTab(VaultTab.OWNER_PANEL) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Owner 500GB",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Owner",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == VaultTab.OWNER_PANEL) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonRed,
                            selectedTextColor = CrimsonRedLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CrimsonContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_owner")
                    )

                    NavigationBarItem(
                        selected = selectedTab == VaultTab.WEB_BROWSER,
                        onClick = { viewModel.selectTab(VaultTab.WEB_BROWSER) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Web Portal",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Portal",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == VaultTab.WEB_BROWSER) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonRed,
                            selectedTextColor = CrimsonRedLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CrimsonContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_portal")
                    )

                    NavigationBarItem(
                        selected = selectedTab == VaultTab.ENCRYPTED_TEXTS,
                        onClick = { viewModel.selectTab(VaultTab.ENCRYPTED_TEXTS) },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Notes,
                                contentDescription = "Encrypted",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Encrypted",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == VaultTab.ENCRYPTED_TEXTS) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonRed,
                            selectedTextColor = CrimsonRedLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CrimsonContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_encrypted")
                    )

                    NavigationBarItem(
                        selected = selectedTab == VaultTab.DEVICES,
                        onClick = { viewModel.selectTab(VaultTab.DEVICES) },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = "Devices",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = "Devices",
                                fontSize = 10.sp,
                                fontWeight = if (selectedTab == VaultTab.DEVICES) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CrimsonRed,
                            selectedTextColor = CrimsonRedLight,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = CrimsonContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_devices")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PitchBlack)
        ) {
            AnimatedContent(
                targetState = authState.stage,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "AuthStageAnimation"
            ) { stage ->
                when (stage) {
                    AuthStage.EMAIL_INPUT, AuthStage.EMAIL_CODE_VERIFY -> {
                        SecureLoginOtpScreen(
                            state = authState,
                            onEmailChanged = { viewModel.onEmailChanged(it) },
                            onPasswordChanged = { viewModel.onPasswordChanged(it) },
                            onSubmitCredentials = { viewModel.submitEmailForVerification() },
                            onOtpCodeChanged = { viewModel.onCodeInputChanged(it) },
                            onFillSampleOtp = { viewModel.fillSampleCode() },
                            onResendOtp = { viewModel.resendCode() },
                            onBackToCredentials = { viewModel.logout() },
                            onRegisterNameChanged = { viewModel.onRegisterNameChanged(it) },
                            onToggleRegisterMode = { viewModel.toggleRegisterMode() },
                            onBiometricLogin = { email -> viewModel.loginWithBiometrics(email) },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }

                    AuthStage.DEVICE_CHALLENGE -> {
                        DeviceVerificationScreen(
                            state = authState,
                            onAuthorizeDevice = { viewModel.authorizeCurrentDevice() },
                            modifier = Modifier.statusBarsPadding()
                        )
                    }

                    AuthStage.PIN_LOCK -> {
                        PinLockScreen(
                            state = authState,
                            onPinDigit = { digit ->
                                val current = authState.pinInput
                                if (current.length < 4) {
                                    viewModel.onPinInputChanged(current + digit)
                                }
                            },
                            onDeleteDigit = {
                                val current = authState.pinInput
                                if (current.isNotEmpty()) {
                                    viewModel.onPinInputChanged(current.dropLast(1))
                                }
                            },
                            onBiometricUnlock = { viewModel.unlockWithBiometrics() }
                        )
                    }

                    AuthStage.AUTHENTICATED -> {
                        when (selectedTab) {
                            VaultTab.CLOUD_FILES -> {
                                CloudDataScreen(
                                    currentUser = authState.currentUser,
                                    files = cloudFiles,
                                    allUsers = users,
                                    onUploadFile = { name, type, sizeMb, snippet ->
                                        viewModel.uploadCloudFile(name, type, sizeMb, snippet)
                                    },
                                    onDeleteFile = { file ->
                                        viewModel.deleteCloudFile(file)
                                    },
                                    onSwitchUser = { email ->
                                        viewModel.switchAccount(email)
                                    },
                                    onUpgradeStorageQuota = { newQuota ->
                                        val email = authState.currentUser?.email ?: authState.email
                                        viewModel.updateUserStorageQuota(email, newQuota)
                                    }
                                )
                            }

                            VaultTab.OWNER_PANEL -> {
                                OwnerCloudConsoleScreen(
                                    currentUser = authState.currentUser,
                                    users = users,
                                    onUpdateStorageQuota = { email, newQuota ->
                                        viewModel.updateUserStorageQuota(email, newQuota)
                                    }
                                )
                            }

                            VaultTab.WEB_BROWSER -> {
                                WebBrowserPortalScreen(
                                    currentEmail = authState.email,
                                    isOwner = authState.currentUser?.isOwner == true
                                )
                            }

                            VaultTab.ENCRYPTED_TEXTS -> {
                                VaultHomeScreen(
                                    items = items,
                                    searchQuery = searchQuery,
                                    selectedCategory = selectedCategory,
                                    revealedItemIds = revealedItemIds,
                                    onSearchChanged = { viewModel.onSearchQueryChanged(it) },
                                    onCategorySelected = { viewModel.onCategorySelected(it) },
                                    onToggleReveal = { id, item -> viewModel.toggleRevealContent(id, item) },
                                    onDecryptContent = { viewModel.decryptItemContent(it) },
                                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                                    onDeleteItem = { viewModel.deleteItem(it) },
                                    onCreateItem = { title, content, cat, tags, isBurn, views ->
                                        viewModel.createItem(title, content, cat, tags, isBurn, views)
                                    },
                                    verifiedDeviceCount = devices.count { it.status == "VERIFIED" },
                                    userEmail = authState.email
                                )
                            }

                            VaultTab.DEVICES -> {
                                DevicesManagerScreen(
                                    devices = devices,
                                    onPairNewDevice = { name, type ->
                                        viewModel.pairNewDevice(name, type)
                                    },
                                    onRevokeDevice = { device ->
                                        viewModel.revokeDevice(device)
                                    },
                                    onDeleteDevice = { device ->
                                        viewModel.deleteDevice(device)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
