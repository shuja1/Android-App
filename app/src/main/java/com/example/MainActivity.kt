package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.domain.BasicCalculatorEngine
import com.example.domain.GstCalculationResult
import com.example.domain.GstCalculatorEngine
import com.example.domain.PdfReportMetadata
import com.example.domain.SupplyType
import com.example.pdf.GstPdfGenerator
import com.example.ui.CalculatorViewModel
import com.example.ui.ToolsViewModel
import com.example.ui.components.ExportPdfBottomSheet
import com.example.ui.screens.BasicCalculatorScreen
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.HistoryAndSlabsScreen
import com.example.ui.screens.PlayStoreDocsScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.theme.ElemensysGstTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ROUTE_GST = "gst_calculator"
private const val ROUTE_BASIC = "basic_calculator"
private const val ROUTE_CONVERTER = "unit_converter"
private const val ROUTE_HISTORY_SLABS = "history_slabs"
private const val ROUTE_PLAY_DOCS = "play_docs"

private data class NavTabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElemensysGstTheme {
                ElemensysGstApp()
            }
        }
    }
}

@Composable
fun ElemensysGstApp(
    viewModel: CalculatorViewModel = viewModel(),
    toolsViewModel: ToolsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val basicState by toolsViewModel.basicState.collectAsStateWithLifecycle()
    val converterState by toolsViewModel.converterState.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_GST

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var exportTargetResult by remember { mutableStateOf<GstCalculationResult?>(null) }
    var exportUsesLiveResult by remember { mutableStateOf(true) }
    var pendingSavePayload by remember { mutableStateOf<Pair<GstCalculationResult, PdfReportMetadata>?>(null) }
    var requestedHistorySubTab by remember { mutableStateOf<Int?>(null) }

    val createPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        val payload = pendingSavePayload
        pendingSavePayload = null
        if (uri != null && payload != null) {
            scope.launch {
                val writeResult = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openOutputStream(uri)?.use { stream ->
                            GstPdfGenerator.writePdfToOutputStream(
                                context = context,
                                result = payload.first,
                                metadata = payload.second,
                                outputStream = stream
                            )
                        } ?: error("Unable to open destination file stream.")
                    }
                }
                if (writeResult.isSuccess) {
                    snackbarHostState.showSnackbar("PDF report saved successfully.")
                } else {
                    snackbarHostState.showSnackbar(
                        "Failed to save PDF: ${writeResult.exceptionOrNull()?.localizedMessage ?: "Unknown error"}"
                    )
                }
            }
        }
    }

    val navItems = remember {
        listOf(
            NavTabItem(
                route = ROUTE_GST,
                label = "GST",
                selectedIcon = Icons.AutoMirrored.Rounded.ReceiptLong,
                unselectedIcon = Icons.AutoMirrored.Outlined.ReceiptLong,
                testTag = "nav_tab_gst"
            ),
            NavTabItem(
                route = ROUTE_BASIC,
                label = "Basic",
                selectedIcon = Icons.Rounded.Calculate,
                unselectedIcon = Icons.Outlined.Calculate,
                testTag = "nav_tab_basic"
            ),
            NavTabItem(
                route = ROUTE_CONVERTER,
                label = "Converter",
                selectedIcon = Icons.Rounded.SwapHoriz,
                unselectedIcon = Icons.Outlined.SwapHoriz,
                testTag = "nav_tab_converter"
            ),
            NavTabItem(
                route = ROUTE_HISTORY_SLABS,
                label = "Saved",
                selectedIcon = Icons.Rounded.History,
                unselectedIcon = Icons.Outlined.History,
                testTag = "nav_tab_history"
            ),
            NavTabItem(
                route = ROUTE_PLAY_DOCS,
                label = "Play Docs",
                selectedIcon = Icons.Rounded.Description,
                unselectedIcon = Icons.Outlined.Description,
                testTag = "nav_tab_play_docs"
            )
        )
    }

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        scope.launch {
            snackbarHostState.showSnackbar("$label copied to clipboard.")
        }
    }

    fun sharePlainText(subject: String, text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share $subject"))
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail(
                        modifier = Modifier.fillMaxHeight(),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag(item.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_GST
                    ) {
                        composable(ROUTE_GST) {
                            CalculatorScreen(
                                uiState = uiState,
                                onAmountChange = viewModel::onAmountChange,
                                onAddQuickAmount = viewModel::addQuickAmount,
                                onClearAmount = viewModel::clearAmount,
                                onSelectStandardRate = viewModel::onSelectStandardRate,
                                onSelectCustomRate = viewModel::onSelectCustomRateMode,
                                onCustomRateChange = viewModel::onCustomRateChange,
                                onCessRateChange = viewModel::onCessRateChange,
                                onQuantityChange = viewModel::onQuantityChange,
                                onDiscountChange = viewModel::onDiscountChange,
                                onTaxModeChange = viewModel::onTaxModeChange,
                                onSupplyTypeChange = viewModel::onSupplyTypeChange,
                                onToggleAdvanced = viewModel::toggleAdvancedOptions,
                                onToggleComparison = viewModel::toggleAllSlabsComparison,
                                onCurrencyChange = { symbol ->
                                    viewModel.onCurrencyChange(symbol)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Currency set to ${symbol.trim()}.")
                                    }
                                },
                                onBrowseHsn = {
                                    requestedHistorySubTab = 2
                                    navigateToTab(ROUTE_HISTORY_SLABS)
                                },
                                onClearActiveHsn = viewModel::clearActiveHsn,
                                onSaveCalculation = {
                                    viewModel.saveCurrentCalculation()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Calculation saved to history.")
                                    }
                                },
                                onCopySummary = {
                                    copyToClipboard(
                                        "GST Calculation Summary",
                                        buildClipboardSummary(uiState.result)
                                    )
                                },
                                onOpenPdfExport = {
                                    exportUsesLiveResult = true
                                    exportTargetResult = uiState.result
                                }
                            )
                        }

                        composable(ROUTE_BASIC) {
                            BasicCalculatorScreen(
                                uiState = basicState,
                                onDigitOrDot = toolsViewModel::onDigitOrDot,
                                onOperator = toolsViewModel::onOperator,
                                onPercent = toolsViewModel::onPercent,
                                onToggleParenthesis = toolsViewModel::onToggleParenthesis,
                                onToggleSign = toolsViewModel::onToggleSign,
                                onBackspace = toolsViewModel::onBackspace,
                                onAllClear = toolsViewModel::onAllClear,
                                onEquals = toolsViewModel::onEquals,
                                onUseHistoryItem = toolsViewModel::onUseHistoryItem,
                                onClearHistory = toolsViewModel::clearBasicHistory,
                                onCopyResult = { value ->
                                    copyToClipboard("Calculation Result", value)
                                },
                                onSendToGst = { numericVal ->
                                    viewModel.setAmountFromExternal(numericVal)
                                    navigateToTab(ROUTE_GST)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Transferred ${BasicCalculatorEngine.formatResult(numericVal)} to GST Calculator."
                                        )
                                    }
                                }
                            )
                        }

                        composable(ROUTE_CONVERTER) {
                            UnitConverterScreen(
                                uiState = converterState,
                                onSelectCategory = toolsViewModel::selectCategory,
                                onInputChange = toolsViewModel::onConverterInputChange,
                                onSelectFromUnit = toolsViewModel::selectFromUnit,
                                onSelectToUnit = toolsViewModel::selectToUnit,
                                onSwapUnits = toolsViewModel::swapConverterUnits,
                                onCopyResult = { value ->
                                    copyToClipboard("Converted Value", value)
                                }
                            )
                        }

                        composable(ROUTE_HISTORY_SLABS) {
                            HistoryAndSlabsScreen(
                                savedHistory = uiState.savedHistory,
                                requestedTabIndex = requestedHistorySubTab,
                                onConsumeRequestedTab = { requestedHistorySubTab = null },
                                onLoadCalculation = { saved ->
                                    viewModel.loadSavedCalculation(saved)
                                    navigateToTab(ROUTE_GST)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Loaded \"${saved.title}\" into calculator.")
                                    }
                                },
                                onExportSavedPdf = { saved ->
                                    exportUsesLiveResult = false
                                    exportTargetResult = viewModel.buildResultFromSaved(saved)
                                },
                                onDeleteCalculation = viewModel::deleteSavedCalculation,
                                onClearAllHistory = {
                                    viewModel.clearAllHistory()
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Saved history cleared.")
                                    }
                                },
                                onApplySlabRate = { rate ->
                                    viewModel.onSelectStandardRate(rate)
                                    navigateToTab(ROUTE_GST)
                                },
                                onApplyHsnProduct = { hsnItem ->
                                    viewModel.applyHsnProduct(hsnItem)
                                    navigateToTab(ROUTE_GST)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            "Applied HSN ${hsnItem.hsnCode} (${GstCalculatorEngine.formatRate(hsnItem.gstRate)}% GST)."
                                        )
                                    }
                                }
                            )
                        }

                        composable(ROUTE_PLAY_DOCS) {
                            PlayStoreDocsScreen(
                                businessProfile = uiState.businessProfile,
                                currencySymbol = uiState.currencySymbol,
                                checklistStates = uiState.checklistStates,
                                onSaveBusinessProfile = { profile ->
                                    viewModel.saveBusinessProfile(profile)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Business profile saved for PDF reports.")
                                    }
                                },
                                onCurrencyChange = { symbol ->
                                    viewModel.onCurrencyChange(symbol)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Currency updated to ${symbol.trim()}.")
                                    }
                                },
                                onToggleChecklist = viewModel::toggleChecklistItem,
                                onCopyDocText = { title, content ->
                                    copyToClipboard(title, content)
                                },
                                onShareDocText = { title, content ->
                                    sharePlainText(title, content)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    val currentSheetResult = if (exportUsesLiveResult && exportTargetResult != null) {
        uiState.result
    } else {
        exportTargetResult
    }
    if (currentSheetResult != null) {
        ExportPdfBottomSheet(
            result = currentSheetResult,
            businessProfile = uiState.businessProfile,
            initialReportTitle = if (exportUsesLiveResult && uiState.activeProductTitle.isNotBlank()) {
                uiState.activeProductTitle
            } else {
                "GST Tax Calculation Report"
            },
            initialHsnCode = if (exportUsesLiveResult) uiState.activeHsnCode else "",
            onCurrencyChange = { newSymbol ->
                viewModel.onCurrencyChange(newSymbol)
                if (!exportUsesLiveResult) {
                    exportTargetResult = currentSheetResult.let { existing ->
                        GstCalculatorEngine.calculate(existing.input.copy(currencySymbol = newSymbol))
                    }
                }
            },
            onDismiss = { exportTargetResult = null },
            onSaveToDevice = { metadata ->
                val fileStamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date())
                pendingSavePayload = currentSheetResult to metadata
                exportTargetResult = null
                createPdfLauncher.launch("Elemensys_GST_Report_$fileStamp.pdf")
            },
            onSharePdf = { metadata ->
                val resultToShare = currentSheetResult
                exportTargetResult = null
                scope.launch {
                    val uriResult = withContext(Dispatchers.IO) {
                        runCatching {
                            GstPdfGenerator.createShareablePdfUri(
                                context = context,
                                result = resultToShare,
                                metadata = metadata
                            )
                        }
                    }
                    uriResult.onSuccess { pdfUri ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, pdfUri)
                            putExtra(Intent.EXTRA_SUBJECT, metadata.reportTitle)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(
                            Intent.createChooser(shareIntent, "Share GST PDF Report")
                        )
                    }.onFailure { err ->
                        snackbarHostState.showSnackbar(
                            "Could not generate PDF: ${err.localizedMessage ?: "Unknown error"}"
                        )
                    }
                }
            }
        )
    }
}

private fun buildClipboardSummary(result: GstCalculationResult): String {
    val currency = result.input.currencySymbol
    val rateStr = GstCalculatorEngine.formatRate(result.input.gstRatePercent)
    val taxLines = if (result.input.supplyType == SupplyType.INTRA_STATE) {
        "CGST (${GstCalculatorEngine.formatRate(result.cgstRatePercent)}%): ${GstCalculatorEngine.formatMoney(result.cgstAmount, currency)}\n" +
            "SGST (${GstCalculatorEngine.formatRate(result.sgstRatePercent)}%): ${GstCalculatorEngine.formatMoney(result.sgstAmount, currency)}"
    } else {
        "IGST (${GstCalculatorEngine.formatRate(result.igstRatePercent)}%): ${GstCalculatorEngine.formatMoney(result.igstAmount, currency)}"
    }
    return buildString {
        appendLine("Elemensys GST Calculator Summary")
        appendLine("Mode: ${result.input.taxMode.label} | Rate: $rateStr%")
        appendLine("Net Base Amount: ${GstCalculatorEngine.formatMoney(result.netTaxableAmount, currency)}")
        appendLine(taxLines)
        appendLine("Total Tax: ${GstCalculatorEngine.formatMoney(result.totalTaxWithCess, currency)}")
        appendLine("Grand Total: ${GstCalculatorEngine.formatMoney(result.grandTotalAmount, currency)}")
        appendLine()
        appendLine("Steps:")
        result.steps.forEach { step ->
            appendLine("${step.stepNumber}. ${step.title}: ${step.formula} -> ${step.resultFormatted}")
        }
    }.trim()
}
