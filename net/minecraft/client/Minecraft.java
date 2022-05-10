package net.minecraft.client;

import ShwepSS.B17.ExtremeHack;
import ShwepSS.event.EventOpenScreen;
import ShwepSS.eventapi.EventManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.profiler.ISnooperInfo;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import viamcp.utils.AttackOrder;

public class Minecraft
implements IThreadListener,
ISnooperInfo {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation LOCATION_MOJANG_PNG = new ResourceLocation("textures/gui/title/mojang.png");
    public static final boolean IS_RUNNING_ON_MAC = Util.getOSType() == Util.EnumOS.OSX;
    public static byte[] memoryReserve = new byte[0xA00000];
    private static final List<DisplayMode> MAC_DISPLAY_MODES = Lists.newArrayList(new DisplayMode(2560, 1600), new DisplayMode(2880, 1800));
    private final File fileResourcepacks;
    private final PropertyMap twitchDetails;
    private final PropertyMap profileProperties;
    public ServerData currentServerData;
    public TextureManager renderEngine;
    private static Minecraft theMinecraft;
    private final DataFixer dataFixer;
    public PlayerControllerMP playerController;
    private boolean fullscreen;
    private final boolean enableGLErrorChecking = true;
    private boolean hasCrashed;
    private CrashReport crashReporter;
    public int displayWidth;
    public int displayHeight;
    private boolean connectedToRealms;
    public final Timer timer = new Timer(20.0f);
    private final Snooper usageSnooper = new Snooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public WorldClient world;
    public RenderGlobal renderGlobal;
    public RenderManager renderManager;
    private RenderItem renderItem;
    private ItemRenderer itemRenderer;
    public EntityPlayerSP player;
    @Nullable
    private Entity renderViewEntity;
    public Entity pointedEntity;
    public ParticleManager effectRenderer;
    private SearchTreeManager field_193995_ae = new SearchTreeManager();
    public Session session;
    private boolean isGamePaused;
    private float field_193996_ah;
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;
    @Nullable
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    public DebugRenderer debugRenderer;
    private int leftClickCounter;
    private final int tempDisplayWidth;
    private final int tempDisplayHeight;
    @Nullable
    public IntegratedServer theIntegratedServer;
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld;
    public RayTraceResult objectMouseOver;
    public GameSettings gameSettings;
    public CreativeSettings field_191950_u;
    public MouseHelper mouseHelper;
    public final File mcDataDir;
    private final File fileAssets;
    private final String launchedVersion;
    private final String versionType;
    private final Proxy proxy;
    private ISaveFormat saveLoader;
    public static int debugFPS;
    public int rightClickDelayTimer;
    private String serverName;
    private int serverPort;
    public boolean inGameHasFocus;
    long systemTime = Minecraft.getSystemTime();
    private int joinPlayerCounter;
    public final FrameTimer frameTimer = new FrameTimer();
    long startNanoTime = System.nanoTime();
    private final boolean jvm64bit;
    private final boolean isDemo;
    @Nullable
    private NetworkManager myNetworkManager;
    private boolean integratedServerIsRunning;
    public final Profiler mcProfiler = new Profiler();
    private long debugCrashKeyPressTime = -1L;
    private IReloadableResourceManager mcResourceManager;
    private final MetadataSerializer metadataSerializer_ = new MetadataSerializer();
    private final List<IResourcePack> defaultResourcePacks = Lists.newArrayList();
    private final DefaultResourcePack mcDefaultResourcePack;
    private ResourcePackRepository mcResourcePackRepository;
    private LanguageManager mcLanguageManager;
    private BlockColors blockColors;
    private ItemColors itemColors;
    private Framebuffer framebufferMc;
    private TextureMap textureMapBlocks;
    private SoundHandler mcSoundHandler;
    private MusicTicker mcMusicTicker;
    private ResourceLocation mojangLogo;
    private final MinecraftSessionService sessionService;
    private SkinManager skinManager;
    private final Queue<FutureTask<?>> scheduledTasks = Queues.newArrayDeque();
    private final Thread mcThread = Thread.currentThread();
    private ModelManager modelManager;
    private BlockRendererDispatcher blockRenderDispatcher;
    private final GuiToast field_193034_aS;
    volatile boolean running = true;
    public String debug = "";
    public boolean renderChunksMany = true;
    private long debugUpdateTime = Minecraft.getSystemTime();
    private int fpsCounter;
    private boolean actionKeyF3;
    private final Tutorial field_193035_aW;
    long prevFrameTime = -1L;
    private String debugProfilerName = "root";

    public Minecraft(GameConfiguration gameConfig) {
        ExtremeHack extreme = new ExtremeHack(this);
        theMinecraft = this;
        this.mcDataDir = gameConfig.folderInfo.mcDataDir;
        this.fileAssets = gameConfig.folderInfo.assetsDir;
        this.fileResourcepacks = gameConfig.folderInfo.resourcePacksDir;
        this.launchedVersion = gameConfig.gameInfo.version;
        this.versionType = gameConfig.gameInfo.versionType;
        this.twitchDetails = gameConfig.userInfo.userProperties;
        this.profileProperties = gameConfig.userInfo.profileProperties;
        this.mcDefaultResourcePack = new DefaultResourcePack(gameConfig.folderInfo.getAssetsIndex());
        this.proxy = gameConfig.userInfo.proxy == null ? Proxy.NO_PROXY : gameConfig.userInfo.proxy;
        this.sessionService = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        this.session = gameConfig.userInfo.session;
        LOGGER.info("Setting user: {}", (Object)this.session.getUsername());
        LOGGER.debug("(Session ID is {})", (Object)this.session.getSessionID());
        this.isDemo = gameConfig.gameInfo.isDemo;
        this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
        this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
        this.tempDisplayWidth = gameConfig.displayInfo.width;
        this.tempDisplayHeight = gameConfig.displayInfo.height;
        this.fullscreen = gameConfig.displayInfo.fullscreen;
        this.jvm64bit = Minecraft.isJvm64bit();
        this.theIntegratedServer = null;
        if (gameConfig.serverInfo.serverName != null) {
            this.serverName = gameConfig.serverInfo.serverName;
            this.serverPort = gameConfig.serverInfo.serverPort;
        }
        ImageIO.setUseCache(false);
        Locale.setDefault(Locale.ROOT);
        Bootstrap.register();
        TextComponentKeybind.field_193637_b = KeyBinding::func_193626_b;
        this.dataFixer = DataFixesManager.createFixer();
        this.field_193034_aS = new GuiToast(this);
        this.field_193035_aW = new Tutorial(this);
    }

    /*
     * Exception decompiling
     */
    public void run() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:406)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:481)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:728)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:806)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:258)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:192)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:521)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:922)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:253)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:135)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         * async.DecompilerRunnable.cfrDecompilation(DecompilerRunnable.java:267)
         * async.DecompilerRunnable.call(DecompilerRunnable.java:228)
         * async.DecompilerRunnable.call(DecompilerRunnable.java:26)
         * java.util.concurrent.FutureTask.run(FutureTask.java:266)
         * java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
         * java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
         * java.lang.Thread.run(Thread.java:748)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    private void startGame() throws LWJGLException, IOException {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        this.field_191950_u = new CreativeSettings(this, this.mcDataDir);
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
        this.startTimerHackThread();
        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }
        LOGGER.info("LWJGL Version: {}", (Object)Sys.getVersion());
        this.setWindowIcon();
        this.setInitialDisplayMode();
        this.createDisplay();
        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.registerMetadataSerializers();
        this.mcResourcePackRepository = new ResourcePackRepository(this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings);
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.drawSplashScreen(this.renderEngine);
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"), this.dataFixer);
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
        if (this.gameSettings.language != null) {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }
        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7425);
        GlStateManager.clearDepth(1.0);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        this.checkGLError("Startup");
        this.textureMapBlocks = new TextureMap("textures");
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.renderEngine.loadTickableTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, this.textureMapBlocks);
        this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
        this.modelManager = new ModelManager(this.textureMapBlocks);
        this.mcResourceManager.registerReloadListener(this.modelManager);
        this.blockColors = BlockColors.init();
        this.itemColors = ItemColors.init(this.blockColors);
        this.renderItem = new RenderItem(this.renderEngine, this.modelManager, this.itemColors);
        this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
        this.itemRenderer = new ItemRenderer(this);
        this.mcResourceManager.registerReloadListener(this.renderItem);
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(), this.blockColors);
        this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
        this.renderGlobal = new RenderGlobal(this);
        this.mcResourceManager.registerReloadListener(this.renderGlobal);
        this.func_193986_ar();
        this.mcResourceManager.registerReloadListener(this.field_193995_ae);
        GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
        this.effectRenderer = new ParticleManager(this.world, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);
        if (this.serverName != null) {
            this.displayGuiScreen(new GuiConnecting(new GuiMainMenu(), this, this.serverName, this.serverPort));
        } else {
            this.displayGuiScreen(new GuiMainMenu());
        }
        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);
        this.debugRenderer = new DebugRenderer(this);
        if (this.gameSettings.fullScreen && !this.fullscreen) {
            this.toggleFullscreen();
        }
        try {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        }
        catch (OpenGLException var2) {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }
        this.renderGlobal.makeEntityOutlineShader();
    }

    private void func_193986_ar() {
        SearchTree<ItemStack> searchtree = new SearchTree<ItemStack>(p_193988_0_ -> p_193988_0_.getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).stream().map(TextFormatting::getTextWithoutFormattingCodes).map(String::trim).filter(p_193984_0_ -> !p_193984_0_.isEmpty()).collect(Collectors.toList()), p_193985_0_ -> Collections.singleton(Item.REGISTRY.getNameForObject(p_193985_0_.getItem())));
        NonNullList<ItemStack> nonnulllist = NonNullList.func_191196_a();
        for (Item item : Item.REGISTRY) {
            item.getSubItems(CreativeTabs.SEARCH, nonnulllist);
        }
        nonnulllist.forEach(searchtree::func_194043_a);
        SearchTree<RecipeList> searchtree1 = new SearchTree<RecipeList>(p_193990_0_ -> p_193990_0_.func_192711_b().stream().flatMap(p_193993_0_ -> p_193993_0_.getRecipeOutput().getTooltip(null, ITooltipFlag.TooltipFlags.NORMAL).stream()).map(TextFormatting::getTextWithoutFormattingCodes).map(String::trim).filter(p_193994_0_ -> !p_193994_0_.isEmpty()).collect(Collectors.toList()), p_193991_0_ -> p_193991_0_.func_192711_b().stream().map(p_193992_0_ -> Item.REGISTRY.getNameForObject(p_193992_0_.getRecipeOutput().getItem())).collect(Collectors.toList()));
        RecipeBookClient.field_194087_f.forEach(searchtree1::func_194043_a);
        this.field_193995_ae.func_194009_a(SearchTreeManager.field_194011_a, searchtree);
        this.field_193995_ae.func_194009_a(SearchTreeManager.field_194012_b, searchtree1);
    }

    private void registerMetadataSerializers() {
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
    }

    private void createDisplay() throws LWJGLException {
        Display.setResizable(true);
        Display.setTitle("ExtremeHack B17 - budet update s fix bagov");
        try {
            Display.create(new PixelFormat().withDepthBits(24));
        }
        catch (LWJGLException lwjglexception) {
            LOGGER.error("Couldn't set pixel format", (Throwable)lwjglexception);
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
            if (this.fullscreen) {
                this.updateDisplayMode();
            }
            Display.create();
        }
    }

    private void setInitialDisplayMode() throws LWJGLException {
        if (this.fullscreen) {
            Display.setFullscreen(true);
            DisplayMode displaymode = Display.getDisplayMode();
            this.displayWidth = Math.max(1, displaymode.getWidth());
            this.displayHeight = Math.max(1, displaymode.getHeight());
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }
    }

    private void setWindowIcon() {
        block7: {
            Util.EnumOS util$enumos = Util.getOSType();
            if (util$enumos != Util.EnumOS.OSX) {
                InputStream inputstream = null;
                InputStream inputstream1 = null;
                try {
                    try {
                        inputstream = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_16x16.png"));
                        inputstream1 = this.mcDefaultResourcePack.getInputStreamAssets(new ResourceLocation("icons/icon_32x32.png"));
                        if (inputstream != null && inputstream1 != null) {
                            Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(inputstream), this.readImageToBuffer(inputstream1)});
                        }
                    }
                    catch (IOException ioexception) {
                        LOGGER.error("Couldn't set icon", (Throwable)ioexception);
                        IOUtils.closeQuietly(inputstream);
                        IOUtils.closeQuietly(inputstream1);
                        break block7;
                    }
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(inputstream);
                    IOUtils.closeQuietly(inputstream1);
                    throw throwable;
                }
                IOUtils.closeQuietly(inputstream);
                IOUtils.closeQuietly(inputstream1);
            }
        }
    }

    private static boolean isJvm64bit() {
        String[] astring;
        String[] arrstring = astring = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        int n2 = astring.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            String s2 = arrstring[i2];
            String s1 = System.getProperty(s2);
            if (s1 == null || !s1.contains("64")) continue;
            return true;
        }
        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.framebufferMc;
    }

    public String getVersion() {
        return this.launchedVersion;
    }

    public String getVersionType() {
        return this.versionType;
    }

    private void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread"){

            @Override
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(Integer.MAX_VALUE);
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport crash) {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    public void displayCrashReport(CrashReport crashReportIn) {
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file1, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-client.txt");
        Bootstrap.printToSYSOUT(crashReportIn.getCompleteReport());
        if (crashReportIn.getFile() != null) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            System.exit(-1);
        } else if (crashReportIn.saveToFile(file2)) {
            Bootstrap.printToSYSOUT("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            Bootstrap.printToSYSOUT("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public boolean isUnicode() {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources() {
        ArrayList<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);
        if (this.theIntegratedServer != null) {
            this.theIntegratedServer.func_193031_aM();
        }
        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            list.add(resourcepackrepository$entry.getResourcePack());
        }
        if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
            list.add(this.mcResourcePackRepository.getResourcePackInstance());
        }
        try {
            this.mcResourceManager.reloadResources(list);
        }
        catch (RuntimeException runtimeexception) {
            LOGGER.info("Caught error stitching, removing all assigned resourcepacks", (Throwable)runtimeexception);
            list.clear();
            list.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setRepositories(Collections.emptyList());
            this.mcResourceManager.reloadResources(list);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.incompatibleResourcePacks.clear();
            this.gameSettings.saveOptions();
        }
        this.mcLanguageManager.parseLanguageMetadata(list);
        if (this.renderGlobal != null) {
            this.renderGlobal.loadRenderers();
        }
    }

    private ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage = ImageIO.read(imageStream);
        int[] aint = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null, 0, bufferedimage.getWidth());
        ByteBuffer bytebuffer = ByteBuffer.allocate(4 * aint.length);
        int[] arrn = aint;
        int n2 = aint.length;
        for (int i2 = 0; i2 < n2; ++i2) {
            int i3 = arrn[i2];
            bytebuffer.putInt(i3 << 8 | i3 >> 24 & 0xFF);
        }
        bytebuffer.flip();
        return bytebuffer;
    }

    private void updateDisplayMode() throws LWJGLException {
        HashSet<DisplayMode> set = Sets.newHashSet();
        Collections.addAll(set, Display.getAvailableDisplayModes());
        DisplayMode displaymode = Display.getDesktopDisplayMode();
        if (!set.contains(displaymode) && Util.getOSType() == Util.EnumOS.OSX) {
            block0: for (DisplayMode displaymode1 : MAC_DISPLAY_MODES) {
                boolean flag = true;
                for (DisplayMode displaymode2 : set) {
                    if (displaymode2.getBitsPerPixel() != 32 || displaymode2.getWidth() != displaymode1.getWidth() || displaymode2.getHeight() != displaymode1.getHeight()) continue;
                    flag = false;
                    break;
                }
                if (flag) continue;
                for (DisplayMode displaymode3 : set) {
                    if (displaymode3.getBitsPerPixel() != 32 || displaymode3.getWidth() != displaymode1.getWidth() / 2 || displaymode3.getHeight() != displaymode1.getHeight() / 2) continue;
                    displaymode = displaymode3;
                    continue block0;
                }
            }
        }
        Display.setDisplayMode(displaymode);
        this.displayWidth = displaymode.getWidth();
        this.displayHeight = displaymode.getHeight();
    }

    private void drawSplashScreen(TextureManager textureManagerInstance) throws LWJGLException {
        Framebuffer framebuffer;
        int i2;
        ScaledResolution scaledresolution;
        block5: {
            scaledresolution = new ScaledResolution(this);
            i2 = scaledresolution.getScaleFactor();
            framebuffer = new Framebuffer(scaledresolution.getScaledWidth() * i2, scaledresolution.getScaledHeight() * i2, true);
            framebuffer.bindFramebuffer(false);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            InputStream inputstream = null;
            try {
                try {
                    inputstream = this.mcDefaultResourcePack.getInputStream(LOCATION_MOJANG_PNG);
                    this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(inputstream)));
                    textureManagerInstance.bindTexture(this.mojangLogo);
                }
                catch (IOException ioexception) {
                    LOGGER.error("Unable to load logo: {}", (Object)LOCATION_MOJANG_PNG, (Object)ioexception);
                    IOUtils.closeQuietly(inputstream);
                    break block5;
                }
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(inputstream);
                throw throwable;
            }
            IOUtils.closeQuietly(inputstream);
        }
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(0.0, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(this.displayWidth, this.displayHeight, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(this.displayWidth, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int j2 = 256;
        int k2 = 256;
        this.draw((scaledresolution.getScaledWidth() - 256) / 2, (scaledresolution.getScaledHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledresolution.getScaledWidth() * i2, scaledresolution.getScaledHeight() * i2);
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1f);
        this.updateDisplay();
    }

    public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue, int alpha) {
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        float f2 = 0.00390625f;
        float f1 = 0.00390625f;
        bufferbuilder.pos(posX, posY + height, 0.0).tex((float)texU * 0.00390625f, (float)(texV + height) * 0.00390625f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY + height, 0.0).tex((float)(texU + width) * 0.00390625f, (float)(texV + height) * 0.00390625f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(posX + width, posY, 0.0).tex((float)(texU + width) * 0.00390625f, (float)texV * 0.00390625f).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(posX, posY, 0.0).tex((float)texU * 0.00390625f, (float)texV * 0.00390625f).color(red, green, blue, alpha).endVertex();
        Tessellator.getInstance().draw();
    }

    public ISaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    public void displayGuiScreen(@Nullable GuiScreen guiScreenIn) {
        EventOpenScreen event = new EventOpenScreen(guiScreenIn);
        EventManager.call(event);
        if (event.isCancelled()) {
            return;
        }
        if (this.currentScreen != null) {
            this.currentScreen.onGuiClosed();
        }
        if (event.getScreen() == null && this.world == null) {
            event.screen = new GuiMainMenu();
        } else if (event.getScreen() == null && this.player.getHealth() <= 0.0f) {
            event.screen = new GuiGameOver(null);
        }
        if (event.getScreen() instanceof GuiMainMenu || event.getScreen() instanceof GuiMultiplayer) {
            this.gameSettings.showDebugInfo = false;
            this.ingameGUI.getChatGUI().clearChatMessages(true);
        }
        this.currentScreen = event.screen;
        if (event.getScreen() != null) {
            this.setIngameNotInFocus();
            KeyBinding.unPressAllKeys();
            while (Mouse.next()) {
            }
            while (Keyboard.next()) {
            }
            ScaledResolution scaledresolution = new ScaledResolution(this);
            int i2 = scaledresolution.getScaledWidth();
            int j2 = scaledresolution.getScaledHeight();
            event.getScreen().setWorldAndResolution(this, i2, j2);
            this.skipRenderWorld = false;
        } else {
            this.mcSoundHandler.resumeSounds();
            this.setIngameFocus();
        }
    }

    private void checkGLError(String message) {
        int i2 = GlStateManager.glGetError();
        if (i2 != 0) {
            String s2 = GLU.gluErrorString(i2);
            LOGGER.error("########## GL ERROR ##########");
            LOGGER.error("@ {}", (Object)message);
            LOGGER.error("{}: {}", (Object)i2, (Object)s2);
        }
    }

    public void shutdownMinecraftApplet() {
        try {
            LOGGER.info("Stopping!");
            try {
                this.loadWorld(null);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            this.mcSoundHandler.unloadSounds();
        }
        finally {
            Display.destroy();
            if (!this.hasCrashed) {
                System.exit(0);
            }
        }
        System.gc();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void runGameLoop() throws IOException {
        boolean flag;
        long i2 = System.nanoTime();
        this.mcProfiler.startSection("root");
        if (Display.isCreated() && Display.isCloseRequested()) {
            this.shutdown();
        }
        this.timer.updateTimer();
        this.mcProfiler.startSection("scheduledExecutables");
        Queue<FutureTask<?>> queue = this.scheduledTasks;
        synchronized (queue) {
            while (!this.scheduledTasks.isEmpty()) {
                Util.runTask(this.scheduledTasks.poll(), LOGGER);
            }
        }
        this.mcProfiler.endSection();
        long l2 = System.nanoTime();
        this.mcProfiler.startSection("tick");
        for (int j2 = 0; j2 < Math.min(10, this.timer.elapsedTicks); ++j2) {
            try {
                this.runTick();
                continue;
            }
            catch (Exception eg2) {
                eg2.printStackTrace();
            }
        }
        this.mcProfiler.endStartSection("preRenderErrors");
        long i1 = System.nanoTime() - l2;
        this.checkGLError("Pre render");
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.player, this.timer.field_194147_b);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GlStateManager.pushMatrix();
        GlStateManager.clear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GlStateManager.enableTexture2D();
        this.mcProfiler.endSection();
        if (!this.skipRenderWorld) {
            this.mcProfiler.endStartSection("gameRenderer");
            this.entityRenderer.updateCameraAndRender(this.isGamePaused ? this.field_193996_ah : this.timer.field_194147_b, i2);
            this.mcProfiler.endStartSection("toasts");
            this.field_193034_aS.func_191783_a(new ScaledResolution(this));
            this.mcProfiler.endSection();
        }
        this.mcProfiler.endSection();
        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart && !this.gameSettings.hideGUI) {
            if (!this.mcProfiler.profilingEnabled) {
                this.mcProfiler.clearProfiling();
            }
            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(i1);
        } else {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }
        this.framebufferMc.unbindFramebuffer();
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GlStateManager.popMatrix();
        GlStateManager.pushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.field_194147_b);
        GlStateManager.popMatrix();
        this.mcProfiler.startSection("root");
        this.updateDisplay();
        Thread.yield();
        this.checkGLError("Post render");
        ++this.fpsCounter;
        boolean bl2 = flag = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
        if (this.isGamePaused != flag) {
            if (this.isGamePaused) {
                this.field_193996_ah = this.timer.field_194147_b;
            } else {
                this.timer.field_194147_b = this.field_193996_ah;
            }
            this.isGamePaused = flag;
        }
        long k2 = System.nanoTime();
        this.frameTimer.addFrame(k2 - this.startNanoTime);
        this.startNanoTime = k2;
        while (Minecraft.getSystemTime() >= this.debugUpdateTime + 1000L) {
            debugFPS = this.fpsCounter;
            Object[] arrobject = new Object[8];
            arrobject[0] = debugFPS;
            arrobject[1] = RenderChunk.renderChunksUpdated;
            arrobject[2] = RenderChunk.renderChunksUpdated == 1 ? "" : "s";
            arrobject[3] = (float)this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() ? "inf" : Integer.valueOf(this.gameSettings.limitFramerate);
            arrobject[4] = this.gameSettings.enableVsync ? " vsync" : "";
            Object object = arrobject[5] = this.gameSettings.fancyGraphics ? "" : " fast";
            arrobject[6] = this.gameSettings.clouds == 0 ? "" : (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds");
            arrobject[7] = OpenGlHelper.useVbo() ? " vbo" : "";
            this.debug = String.format("%d fps (%d chunk update%s) T: %s%s%s%s%s", arrobject);
            RenderChunk.renderChunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();
            if (this.usageSnooper.isSnooperRunning()) continue;
            this.usageSnooper.startSnooper();
        }
        if (this.isFramerateLimitBelowMax()) {
            this.mcProfiler.startSection("fpslimit_wait");
            Display.sync(this.getLimitFramerate());
            this.mcProfiler.endSection();
        }
        this.mcProfiler.endSection();
    }

    public void updateDisplay() {
        this.mcProfiler.startSection("display_update");
        Display.update();
        this.mcProfiler.endSection();
        this.checkWindowResize();
    }

    protected void checkWindowResize() {
        if (!this.fullscreen && Display.wasResized()) {
            int i2 = this.displayWidth;
            int j2 = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();
            if (this.displayWidth != i2 || this.displayHeight != j2) {
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }
                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate() {
        return this.world == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
    }

    public boolean isFramerateLimitBelowMax() {
        return (float)this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory() {
        try {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            System.gc();
            this.loadWorld(null);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    private void updateDebugProfilerName(int keyCount) {
        List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
        if (!list.isEmpty()) {
            Profiler.Result profiler$result = list.remove(0);
            if (keyCount == 0) {
                int i2;
                if (!profiler$result.profilerName.isEmpty() && (i2 = this.debugProfilerName.lastIndexOf(46)) >= 0) {
                    this.debugProfilerName = this.debugProfilerName.substring(0, i2);
                }
            } else if (--keyCount < list.size() && !"unspecified".equals(list.get((int)keyCount).profilerName)) {
                if (!this.debugProfilerName.isEmpty()) {
                    this.debugProfilerName = String.valueOf(this.debugProfilerName) + ".";
                }
                this.debugProfilerName = String.valueOf(this.debugProfilerName) + list.get((int)keyCount).profilerName;
            }
        }
    }

    private void displayDebugInfo(long elapsedTicksTime) {
        if (this.mcProfiler.profilingEnabled) {
            List<Profiler.Result> list = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Profiler.Result profiler$result = list.remove(0);
            GlStateManager.clear(256);
            GlStateManager.matrixMode(5889);
            GlStateManager.enableColorMaterial();
            GlStateManager.loadIdentity();
            GlStateManager.ortho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0f, 0.0f, -2000.0f);
            GlStateManager.glLineWidth(1.0f);
            GlStateManager.disableTexture2D();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            int i2 = 160;
            int j2 = this.displayWidth - 160 - 10;
            int k2 = this.displayHeight - 320;
            GlStateManager.enableBlend();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
            bufferbuilder.pos((float)j2 - 176.0f, (float)k2 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
            bufferbuilder.pos((float)j2 - 176.0f, k2 + 320, 0.0).color(200, 0, 0, 0).endVertex();
            bufferbuilder.pos((float)j2 + 176.0f, k2 + 320, 0.0).color(200, 0, 0, 0).endVertex();
            bufferbuilder.pos((float)j2 + 176.0f, (float)k2 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            double d0 = 0.0;
            for (int l2 = 0; l2 < list.size(); ++l2) {
                Profiler.Result profiler$result1 = list.get(l2);
                int i1 = MathHelper.floor(profiler$result1.usePercentage / 4.0) + 1;
                bufferbuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                int j1 = profiler$result1.getColor();
                int k1 = j1 >> 16 & 0xFF;
                int l1 = j1 >> 8 & 0xFF;
                int i22 = j1 & 0xFF;
                bufferbuilder.pos(j2, k2, 0.0).color(k1, l1, i22, 255).endVertex();
                for (int j22 = i1; j22 >= 0; --j22) {
                    float f2 = (float)((d0 + profiler$result1.usePercentage * (double)j22 / (double)i1) * (Math.PI * 2) / 100.0);
                    float f1 = MathHelper.sin(f2) * 160.0f;
                    float f22 = MathHelper.cos(f2) * 160.0f * 0.5f;
                    bufferbuilder.pos((float)j2 + f1, (float)k2 - f22, 0.0).color(k1, l1, i22, 255).endVertex();
                }
                tessellator.draw();
                bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
                for (int i3 = i1; i3 >= 0; --i3) {
                    float f3 = (float)((d0 + profiler$result1.usePercentage * (double)i3 / (double)i1) * (Math.PI * 2) / 100.0);
                    float f4 = MathHelper.sin(f3) * 160.0f;
                    float f5 = MathHelper.cos(f3) * 160.0f * 0.5f;
                    bufferbuilder.pos((float)j2 + f4, (float)k2 - f5, 0.0).color(k1 >> 1, l1 >> 1, i22 >> 1, 255).endVertex();
                    bufferbuilder.pos((float)j2 + f4, (float)k2 - f5 + 10.0f, 0.0).color(k1 >> 1, l1 >> 1, i22 >> 1, 255).endVertex();
                }
                tessellator.draw();
                d0 += profiler$result1.usePercentage;
            }
            DecimalFormat decimalformat = new DecimalFormat("##0.00");
            GlStateManager.enableTexture2D();
            String s2 = "";
            if (!"unspecified".equals(profiler$result.profilerName)) {
                s2 = String.valueOf(s2) + "[0] ";
            }
            s2 = profiler$result.profilerName.isEmpty() ? String.valueOf(s2) + "ROOT " : String.valueOf(s2) + profiler$result.profilerName + ' ';
            int l2 = 0xFFFFFF;
            this.fontRendererObj.drawStringWithShadow(s2, j2 - 160, k2 - 80 - 16, 0xFFFFFF);
            s2 = String.valueOf(decimalformat.format(profiler$result.totalUsePercentage)) + "%";
            this.fontRendererObj.drawStringWithShadow(s2, j2 + 160 - this.fontRendererObj.getStringWidth(s2), k2 - 80 - 16, 0xFFFFFF);
            for (int k22 = 0; k22 < list.size(); ++k22) {
                Profiler.Result profiler$result2 = list.get(k22);
                StringBuilder stringbuilder = new StringBuilder();
                if ("unspecified".equals(profiler$result2.profilerName)) {
                    stringbuilder.append("[?] ");
                } else {
                    stringbuilder.append("[").append(k22 + 1).append("] ");
                }
                String s1 = stringbuilder.append(profiler$result2.profilerName).toString();
                this.fontRendererObj.drawStringWithShadow(s1, j2 - 160, k2 + 80 + k22 * 8 + 20, profiler$result2.getColor());
                s1 = String.valueOf(decimalformat.format(profiler$result2.usePercentage)) + "%";
                this.fontRendererObj.drawStringWithShadow(s1, j2 + 160 - 50 - this.fontRendererObj.getStringWidth(s1), k2 + 80 + k22 * 8 + 20, profiler$result2.getColor());
                s1 = String.valueOf(decimalformat.format(profiler$result2.totalUsePercentage)) + "%";
                this.fontRendererObj.drawStringWithShadow(s1, j2 + 160 - this.fontRendererObj.getStringWidth(s1), k2 + 80 + k22 * 8 + 20, profiler$result2.getColor());
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }

    public void setIngameFocus() {
        if (Display.isActive() && !this.inGameHasFocus) {
            if (!IS_RUNNING_ON_MAC) {
                KeyBinding.updateKeyBindState();
            }
            this.inGameHasFocus = true;
            this.mouseHelper.grabMouseCursor();
            this.displayGuiScreen(null);
            this.leftClickCounter = 10000;
        }
    }

    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    public void displayInGameMenu() {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new GuiIngameMenu());
            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    private void sendClickBlockToController(boolean leftClick) {
        if (!leftClick) {
            this.leftClickCounter = 0;
        }
        if (this.leftClickCounter <= 0 && !this.player.isHandActive()) {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR && this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
                    this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
                    this.player.swingArm(EnumHand.MAIN_HAND);
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    private void clickMouse() {
        if (this.leftClickCounter <= 0) {
            if (this.objectMouseOver == null) {
                LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
                if (this.playerController.isNotCreative()) {
                    this.leftClickCounter = 10;
                }
            } else if (!this.player.isRowingBoat()) {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY: {
                        AttackOrder.sendFixedAttack(this.player, this.objectMouseOver.entityHit, EnumHand.MAIN_HAND);
                        break;
                    }
                    case BLOCK: {
                        BlockPos blockpos = this.objectMouseOver.getBlockPos();
                        if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR) {
                            this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
                            break;
                        }
                    }
                    case MISS: {
                        if (this.playerController.isNotCreative()) {
                            this.leftClickCounter = 10;
                        }
                        this.player.resetCooldown();
                    }
                }
                AttackOrder.sendConditionalSwing(this.objectMouseOver, EnumHand.MAIN_HAND);
            }
        }
    }

    public void rightClickMouse() {
        if (!this.playerController.getIsHittingBlock()) {
            this.rightClickDelayTimer = 4;
            if (!this.player.isRowingBoat()) {
                if (this.objectMouseOver == null) {
                    LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
                }
                for (EnumHand enumhand : EnumHand.values()) {
                    ItemStack itemstack = this.player.getHeldItem(enumhand);
                    if (this.objectMouseOver != null) {
                        switch (this.objectMouseOver.typeOfHit) {
                            case ENTITY: {
                                if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit, this.objectMouseOver, enumhand) == EnumActionResult.SUCCESS) {
                                    return;
                                }
                                if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit, enumhand) != EnumActionResult.SUCCESS) break;
                                return;
                            }
                            case BLOCK: {
                                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                                if (this.world.getBlockState(blockpos).getMaterial() == Material.AIR) break;
                                int i2 = itemstack.func_190916_E();
                                EnumActionResult enumactionresult = this.playerController.processRightClickBlock(this.player, this.world, blockpos, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec, enumhand);
                                if (enumactionresult != EnumActionResult.SUCCESS) break;
                                this.player.swingArm(enumhand);
                                if (!itemstack.func_190926_b() && (itemstack.func_190916_E() != i2 || this.playerController.isInCreativeMode())) {
                                    this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                                }
                                return;
                            }
                        }
                    }
                    if (itemstack.func_190926_b() || this.playerController.processRightClick(this.player, this.world, enumhand) != EnumActionResult.SUCCESS) continue;
                    this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
                    return;
                }
            }
        }
    }

    public void toggleFullscreen() {
        try {
            this.gameSettings.fullScreen = this.fullscreen = !this.fullscreen;
            if (this.fullscreen) {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }
                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }
                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            }
            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            } else {
                this.updateFramebufferSize();
            }
            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.updateDisplay();
        }
        catch (Exception exception) {
            LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception);
        }
    }

    private void resize(int width, int height) {
        this.displayWidth = Math.max(1, width);
        this.displayHeight = Math.max(1, height);
        if (this.currentScreen != null) {
            ScaledResolution scaledresolution = new ScaledResolution(this);
            this.currentScreen.onResize(this, scaledresolution.getScaledWidth(), scaledresolution.getScaledHeight());
        }
        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    private void updateFramebufferSize() {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
        if (this.entityRenderer != null) {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    public MusicTicker getMusicTicker() {
        return this.mcMusicTicker;
    }

    public void runTick() throws IOException {
        if (this.rightClickDelayTimer > 0) {
            --this.rightClickDelayTimer;
        }
        this.mcProfiler.startSection("gui");
        if (!this.isGamePaused) {
            this.ingameGUI.updateTick();
        }
        this.mcProfiler.endSection();
        this.entityRenderer.getMouseOver(1.0f);
        this.field_193035_aW.func_193297_a(this.world, this.objectMouseOver);
        this.mcProfiler.startSection("gameMode");
        if (!this.isGamePaused && this.world != null) {
            this.playerController.updateController();
        }
        this.mcProfiler.endStartSection("textures");
        if (this.world != null) {
            this.renderEngine.tick();
        }
        if (this.currentScreen == null && this.player != null) {
            if (this.player.getHealth() <= 0.0f && !(this.currentScreen instanceof GuiGameOver)) {
                this.displayGuiScreen(null);
            } else if (this.player.isPlayerSleeping() && this.world != null) {
                this.displayGuiScreen(new GuiSleepMP());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.player.isPlayerSleeping()) {
            this.displayGuiScreen(null);
        }
        if (this.currentScreen != null) {
            this.leftClickCounter = 10000;
        }
        if (this.currentScreen != null) {
            try {
                this.currentScreen.handleInput();
            }
            catch (Throwable throwable1) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
                crashreportcategory.setDetail("Screen name", new ICrashReportDetail<String>(){

                    @Override
                    public String call() throws Exception {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(crashreport);
            }
            if (this.currentScreen != null) {
                try {
                    this.currentScreen.updateScreen();
                }
                catch (Throwable throwable) {
                    CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
                    CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
                    crashreportcategory1.setDetail("Screen name", new ICrashReportDetail<String>(){

                        @Override
                        public String call() throws Exception {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(crashreport1);
                }
            }
        }
        if (this.currentScreen == null || this.currentScreen.allowUserInput) {
            this.mcProfiler.endStartSection("mouse");
            this.runTickMouse();
            if (this.leftClickCounter > 0) {
                --this.leftClickCounter;
            }
            this.mcProfiler.endStartSection("keyboard");
            this.runTickKeyboard();
        }
        if (this.world != null) {
            if (this.player != null) {
                ++this.joinPlayerCounter;
                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.world.joinEntityInSurroundings(this.player);
                }
            }
            this.mcProfiler.endStartSection("gameRenderer");
            if (!this.isGamePaused) {
                this.entityRenderer.updateRenderer();
            }
            this.mcProfiler.endStartSection("levelRenderer");
            if (!this.isGamePaused) {
                try {
                    this.renderGlobal.updateClouds();
                }
                catch (Exception eg2) {
                    eg2.printStackTrace();
                }
            }
            this.mcProfiler.endStartSection("level");
            if (!this.isGamePaused) {
                if (this.world.getLastLightningBolt() > 0) {
                    this.world.setLastLightningBolt(this.world.getLastLightningBolt() - 1);
                }
                this.world.updateEntities();
            }
        } else if (this.entityRenderer.isShaderActive()) {
            this.entityRenderer.stopUseShader();
        }
        if (!this.isGamePaused) {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }
        if (this.world != null) {
            if (!this.isGamePaused) {
                this.world.setAllowedSpawnTypes(this.world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                this.field_193035_aW.func_193303_d();
                try {
                    this.world.tick();
                }
                catch (Throwable throwable2) {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");
                    if (this.world == null) {
                        CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
                        crashreportcategory2.addCrashSection("Problem", "Level is null!");
                    } else {
                        this.world.addWorldInfoToCrashReport(crashreport2);
                    }
                    throw new ReportedException(crashreport2);
                }
            }
            this.mcProfiler.endStartSection("animateTick");
            if (!this.isGamePaused && this.world != null) {
                this.world.doVoidFogParticles(MathHelper.floor(this.player.posX), MathHelper.floor(this.player.posY), MathHelper.floor(this.player.posZ));
            }
            this.mcProfiler.endStartSection("particles");
            if (!this.isGamePaused) {
                this.effectRenderer.updateEffects();
            }
        } else if (this.myNetworkManager != null) {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }
        this.mcProfiler.endSection();
        this.systemTime = Minecraft.getSystemTime();
    }

    private void runTickKeyboard() throws IOException {
        while (Keyboard.next()) {
            boolean flag;
            int i2;
            int n2 = i2 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            if (this.debugCrashKeyPressTime > 0L) {
                if (Minecraft.getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                    throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                }
                if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                    this.debugCrashKeyPressTime = -1L;
                }
            } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                this.actionKeyF3 = true;
                this.debugCrashKeyPressTime = Minecraft.getSystemTime();
            }
            this.dispatchKeypresses();
            if (this.currentScreen != null) {
                this.currentScreen.handleKeyboardInput();
            }
            if (flag = Keyboard.getEventKeyState()) {
                if (i2 == 62 && this.entityRenderer != null) {
                    this.entityRenderer.switchUseShader();
                }
                boolean flag1 = false;
                if (this.currentScreen == null) {
                    if (i2 == 1) {
                        this.displayInGameMenu();
                    }
                    flag1 = Keyboard.isKeyDown(61) && this.processKeyF3(i2);
                    this.actionKeyF3 |= flag1;
                    if (i2 == 59) {
                        boolean bl2 = this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                    }
                }
                if (flag1) {
                    KeyBinding.setKeyBindState(i2, false);
                } else {
                    KeyBinding.setKeyBindState(i2, true);
                    KeyBinding.onTick(i2);
                }
                if (!this.gameSettings.showDebugProfilerChart) continue;
                if (i2 == 11) {
                    this.updateDebugProfilerName(0);
                }
                for (int j2 = 0; j2 < 9; ++j2) {
                    if (i2 != 2 + j2) continue;
                    this.updateDebugProfilerName(j2 + 1);
                }
                continue;
            }
            KeyBinding.setKeyBindState(i2, false);
            if (i2 != 61) continue;
            if (this.actionKeyF3) {
                this.actionKeyF3 = false;
                continue;
            }
            this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
            this.gameSettings.showDebugProfilerChart = this.gameSettings.showDebugInfo && GuiScreen.isShiftKeyDown();
            boolean bl3 = this.gameSettings.showLagometer = this.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown();
        }
        this.processKeyBinds();
    }

    private boolean processKeyF3(int p_184122_1_) {
        if (p_184122_1_ == 30) {
            this.renderGlobal.loadRenderers();
            this.func_190521_a("debug.reload_chunks.message", new Object[0]);
            return true;
        }
        if (p_184122_1_ == 48) {
            boolean flag1 = !this.renderManager.isDebugBoundingBox();
            this.renderManager.setDebugBoundingBox(flag1);
            this.func_190521_a(flag1 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off", new Object[0]);
            return true;
        }
        if (p_184122_1_ == 32) {
            if (this.ingameGUI != null) {
                this.ingameGUI.getChatGUI().clearChatMessages(false);
            }
            return true;
        }
        if (p_184122_1_ == 33) {
            this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
            this.func_190521_a("debug.cycle_renderdistance.message", this.gameSettings.renderDistanceChunks);
            return true;
        }
        if (p_184122_1_ == 34) {
            boolean flag = this.debugRenderer.toggleDebugScreen();
            this.func_190521_a(flag ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off", new Object[0]);
            return true;
        }
        if (p_184122_1_ == 35) {
            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
            this.func_190521_a(this.gameSettings.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off", new Object[0]);
            this.gameSettings.saveOptions();
            return true;
        }
        if (p_184122_1_ == 49) {
            if (!this.player.canCommandSenderUseCommand(2, "")) {
                this.func_190521_a("debug.creative_spectator.error", new Object[0]);
            } else if (this.player.isCreative()) {
                this.player.sendChatMessage("/gamemode spectator");
            } else if (this.player.isSpectator()) {
                this.player.sendChatMessage("/gamemode creative");
            }
            return true;
        }
        if (p_184122_1_ == 25) {
            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
            this.gameSettings.saveOptions();
            this.func_190521_a(this.gameSettings.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off", new Object[0]);
            return true;
        }
        if (p_184122_1_ == 16) {
            this.func_190521_a("debug.help.message", new Object[0]);
            GuiNewChat guinewchat = this.ingameGUI.getChatGUI();
            guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_chunks.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.show_hitboxes.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.clear_chat.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.cycle_renderdistance.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.chunk_boundaries.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.advanced_tooltips.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.creative_spectator.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.pause_focus.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.help.help", new Object[0]));
            guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_resourcepacks.help", new Object[0]));
            return true;
        }
        if (p_184122_1_ == 20) {
            this.func_190521_a("debug.reload_resourcepacks.message", new Object[0]);
            this.refreshResources();
            return true;
        }
        return false;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private void processKeyBinds() {
        block25: {
            while (this.gameSettings.keyBindTogglePerspective.isPressed()) {
                ++this.gameSettings.thirdPersonView;
                if (this.gameSettings.thirdPersonView > 2) {
                    this.gameSettings.thirdPersonView = 0;
                }
                if (this.gameSettings.thirdPersonView == 0) {
                    this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
                } else if (this.gameSettings.thirdPersonView == 1) {
                    this.entityRenderer.loadEntityShader(null);
                }
                this.renderGlobal.setDisplayListEntitiesDirty();
            }
            while (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                v0 = this.gameSettings.smoothCamera = this.gameSettings.smoothCamera == false;
            }
            for (i = 0; i < 9; ++i) {
                flag = this.gameSettings.field_193629_ap.isKeyDown();
                flag1 = this.gameSettings.field_193630_aq.isKeyDown();
                if (!this.gameSettings.keyBindsHotbar[i].isPressed()) continue;
                if (this.player.isSpectator()) {
                    this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
                    continue;
                }
                if (!this.player.isCreative() || this.currentScreen != null || !flag1 && !flag) {
                    this.player.inventory.currentItem = i;
                    continue;
                }
                GuiContainerCreative.func_192044_a(this, i, flag1, flag);
            }
            while (this.gameSettings.keyBindInventory.isPressed()) {
                if (this.playerController.isRidingHorse()) {
                    this.player.sendHorseInventory();
                    continue;
                }
                this.field_193035_aW.func_193296_a();
                this.displayGuiScreen(new GuiInventory(this.player));
            }
            while (this.gameSettings.field_194146_ao.isPressed()) {
                this.displayGuiScreen(new GuiScreenAdvancements(this.player.connection.func_191982_f()));
            }
            while (this.gameSettings.keyBindSwapHands.isPressed()) {
                if (this.player.isSpectator()) continue;
                this.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
            while (this.gameSettings.keyBindDrop.isPressed()) {
                if (this.player.isSpectator()) continue;
                this.player.dropItem(GuiScreen.isCtrlKeyDown());
            }
            v1 = flag2 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;
            if (flag2) {
                while (this.gameSettings.keyBindChat.isPressed()) {
                    this.displayGuiScreen(new GuiChat());
                }
                if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed()) {
                    this.displayGuiScreen(new GuiChat("/"));
                }
            }
            if (!this.player.isHandActive()) ** GOTO lbl64
            if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
                this.playerController.onStoppedUsingItem(this.player);
            }
            while (this.gameSettings.keyBindAttack.isPressed()) {
            }
            while (this.gameSettings.keyBindUseItem.isPressed()) {
            }
            while (this.gameSettings.keyBindPickBlock.isPressed()) {
            }
            break block25;
lbl-1000:
            // 1 sources

            {
                this.clickMouse();
lbl64:
                // 2 sources

                ** while (this.gameSettings.keyBindAttack.isPressed())
            }
lbl65:
            // 2 sources

            while (this.gameSettings.keyBindUseItem.isPressed()) {
                this.rightClickMouse();
            }
            while (this.gameSettings.keyBindPickBlock.isPressed()) {
                this.middleClickMouse();
            }
        }
        if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0 && !this.player.isHandActive()) {
            this.rightClickMouse();
        }
        this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() != false && this.inGameHasFocus != false);
    }

    private void runTickMouse() throws IOException {
        while (Mouse.next()) {
            long j2;
            int i2 = Mouse.getEventButton();
            KeyBinding.setKeyBindState(i2 - 100, Mouse.getEventButtonState());
            if (Mouse.getEventButtonState()) {
                if (this.player.isSpectator() && i2 == 2) {
                    this.ingameGUI.getSpectatorGui().onMiddleClick();
                } else {
                    KeyBinding.onTick(i2 - 100);
                }
            }
            if ((j2 = Minecraft.getSystemTime() - this.systemTime) > 200L) continue;
            int k2 = Mouse.getEventDWheel();
            if (k2 != 0) {
                if (this.player.isSpectator()) {
                    int n2 = k2 = k2 < 0 ? -1 : 1;
                    if (this.ingameGUI.getSpectatorGui().isMenuActive()) {
                        this.ingameGUI.getSpectatorGui().onMouseScroll(-k2);
                    } else {
                        float f2 = MathHelper.clamp(this.player.capabilities.getFlySpeed() + (float)k2 * 0.005f, 0.0f, 0.2f);
                        this.player.capabilities.setFlySpeed(f2);
                    }
                } else {
                    this.player.inventory.changeCurrentItem(k2);
                }
            }
            if (this.currentScreen == null) {
                if (this.inGameHasFocus || !Mouse.getEventButtonState()) continue;
                this.setIngameFocus();
                continue;
            }
            if (this.currentScreen == null) continue;
            this.currentScreen.handleMouseInput();
        }
    }

    private void func_190521_a(String p_190521_1_, Object ... p_190521_2_) {
        this.ingameGUI.getChatGUI().printChatMessage(new TextComponentString("").appendSibling(new TextComponentTranslation("debug.prefix", new Object[0]).setStyle(new Style().setColor(TextFormatting.YELLOW).setBold(true))).appendText(" ").appendSibling(new TextComponentTranslation(p_190521_1_, p_190521_2_)));
    }

    public void launchIntegratedServer(String folderName, String worldName, @Nullable WorldSettings worldSettingsIn) {
        this.loadWorld(null);
        System.gc();
        ISaveHandler isavehandler = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo worldinfo = isavehandler.loadWorldInfo();
        if (worldinfo == null && worldSettingsIn != null) {
            worldinfo = new WorldInfo(worldSettingsIn, folderName);
            isavehandler.saveWorldInfo(worldinfo);
        }
        if (worldSettingsIn == null) {
            worldSettingsIn = new WorldSettings(worldinfo);
        }
        try {
            YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.mcDataDir, MinecraftServer.USER_CACHE_FILE.getName()));
            TileEntitySkull.setProfileCache(playerprofilecache);
            TileEntitySkull.setSessionService(minecraftsessionservice);
            PlayerProfileCache.setOnlineMode(false);
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn, yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, playerprofilecache);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Starting integrated server");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Starting integrated server");
            crashreportcategory.addCrashSection("Level ID", folderName);
            crashreportcategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(crashreport);
        }
        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel", new Object[0]));
        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            String s2 = this.theIntegratedServer.getUserMessage();
            if (s2 != null) {
                this.loadingScreen.displayLoadingString(I18n.format(s2, new Object[0]));
            } else {
                this.loadingScreen.displayLoadingString("");
            }
            try {
                Thread.sleep(200L);
            }
            catch (InterruptedException crashreport) {
                // empty catch block
            }
        }
        this.displayGuiScreen(new GuiScreenWorking());
        SocketAddress socketaddress = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager networkmanager = NetworkManager.provideLocalClient(socketaddress);
        networkmanager.setNetHandler(new NetHandlerLoginClient(networkmanager, this, null));
        networkmanager.sendPacket(new C00Handshake(socketaddress.toString(), 0, EnumConnectionState.LOGIN));
        networkmanager.sendPacket(new CPacketLoginStart(this.getSession().getProfile()));
        this.myNetworkManager = networkmanager;
    }

    public void loadWorld(@Nullable WorldClient worldClientIn) {
        this.loadWorld(worldClientIn, "");
    }

    public void loadWorld(@Nullable WorldClient worldClientIn, String loadingMessage) {
        if (worldClientIn == null) {
            NetHandlerPlayClient nethandlerplayclient = this.getConnection();
            if (nethandlerplayclient != null) {
                nethandlerplayclient.cleanup();
            }
            if (this.theIntegratedServer != null && this.theIntegratedServer.isAnvilFileSet()) {
                this.theIntegratedServer.initiateShutdown();
            }
            this.theIntegratedServer = null;
            this.entityRenderer.func_190564_k();
            this.playerController = null;
            NarratorChatListener.field_193643_a.func_193642_b();
        }
        this.renderViewEntity = null;
        this.myNetworkManager = null;
        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }
        if (worldClientIn == null && this.world != null) {
            this.mcResourcePackRepository.clearResourcePack();
            this.ingameGUI.resetPlayersOverlayFooterHeader();
            this.setServerData(null);
            this.integratedServerIsRunning = false;
        }
        this.mcSoundHandler.stopSounds();
        this.world = worldClientIn;
        if (this.renderGlobal != null) {
            this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
        }
        if (this.effectRenderer != null) {
            this.effectRenderer.clearEffects(worldClientIn);
        }
        TileEntityRendererDispatcher.instance.setWorld(worldClientIn);
        if (worldClientIn != null) {
            if (!this.integratedServerIsRunning) {
                YggdrasilAuthenticationService authenticationservice = new YggdrasilAuthenticationService(this.proxy, UUID.randomUUID().toString());
                MinecraftSessionService minecraftsessionservice = authenticationservice.createMinecraftSessionService();
                GameProfileRepository gameprofilerepository = authenticationservice.createProfileRepository();
                PlayerProfileCache playerprofilecache = new PlayerProfileCache(gameprofilerepository, new File(this.mcDataDir, MinecraftServer.USER_CACHE_FILE.getName()));
                TileEntitySkull.setProfileCache(playerprofilecache);
                TileEntitySkull.setSessionService(minecraftsessionservice);
                PlayerProfileCache.setOnlineMode(false);
            }
            if (this.player == null) {
                this.player = this.playerController.func_192830_a(worldClientIn, new StatisticsManager(), new RecipeBookClient());
                this.playerController.flipPlayer(this.player);
            }
            this.player.preparePlayerToSpawn();
            worldClientIn.spawnEntityInWorld(this.player);
            this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.player);
            this.renderViewEntity = this.player;
        } else {
            this.saveLoader.flushCache();
            this.player = null;
        }
        System.gc();
        this.systemTime = 0L;
    }

    public void setDimensionAndSpawnPlayer(int dimension) {
        this.world.setInitialSpawnLocation();
        this.world.removeAllEntities();
        int i2 = 0;
        String s2 = null;
        if (this.player != null) {
            i2 = this.player.getEntityId();
            this.world.removeEntity(this.player);
            s2 = this.player.getServerBrand();
        }
        this.renderViewEntity = null;
        EntityPlayerSP entityplayersp = this.player;
        this.player = this.playerController.func_192830_a(this.world, this.player == null ? new StatisticsManager() : this.player.getStatFileWriter(), this.player == null ? new RecipeBook() : this.player.func_192035_E());
        this.player.getDataManager().setEntryValues(entityplayersp.getDataManager().getAll());
        this.player.dimension = dimension;
        this.renderViewEntity = this.player;
        this.player.preparePlayerToSpawn();
        this.player.setServerBrand(s2);
        this.world.spawnEntityInWorld(this.player);
        this.playerController.flipPlayer(this.player);
        this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.player.setEntityId(i2);
        this.playerController.setPlayerCapabilities(this.player);
        this.player.setReducedDebug(entityplayersp.hasReducedDebug());
        if (this.currentScreen instanceof GuiGameOver) {
            this.displayGuiScreen(null);
        }
    }

    public final boolean isDemo() {
        return this.isDemo;
    }

    @Nullable
    public NetHandlerPlayClient getConnection() {
        return this.player == null ? null : this.player.connection;
    }

    public static boolean isGuiEnabled() {
        return theMinecraft == null || !Minecraft.theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() {
        return theMinecraft != null && Minecraft.theMinecraft.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return theMinecraft != null && Minecraft.theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    private void middleClickMouse() {
        if (this.objectMouseOver != null && this.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS) {
            ItemStack itemstack;
            boolean flag = this.player.capabilities.isCreativeMode;
            TileEntity tileentity = null;
            if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos blockpos = this.objectMouseOver.getBlockPos();
                IBlockState iblockstate = this.world.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if (iblockstate.getMaterial() == Material.AIR) {
                    return;
                }
                itemstack = block.getItem(this.world, blockpos, iblockstate);
                if (itemstack.func_190926_b()) {
                    return;
                }
                if (flag && GuiScreen.isCtrlKeyDown() && block.hasTileEntity()) {
                    tileentity = this.world.getTileEntity(blockpos);
                }
            } else {
                if (this.objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY || this.objectMouseOver.entityHit == null || !flag) {
                    return;
                }
                if (this.objectMouseOver.entityHit instanceof EntityPainting) {
                    itemstack = new ItemStack(Items.PAINTING);
                } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
                    itemstack = new ItemStack(Items.LEAD);
                } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    EntityItemFrame entityitemframe = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack itemstack1 = entityitemframe.getDisplayedItem();
                    itemstack = itemstack1.func_190926_b() ? new ItemStack(Items.ITEM_FRAME) : itemstack1.copy();
                } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
                    Item item1;
                    EntityMinecart entityminecart = (EntityMinecart)this.objectMouseOver.entityHit;
                    switch (entityminecart.getType()) {
                        case FURNACE: {
                            item1 = Items.FURNACE_MINECART;
                            break;
                        }
                        case CHEST: {
                            item1 = Items.CHEST_MINECART;
                            break;
                        }
                        case TNT: {
                            item1 = Items.TNT_MINECART;
                            break;
                        }
                        case HOPPER: {
                            item1 = Items.HOPPER_MINECART;
                            break;
                        }
                        case COMMAND_BLOCK: {
                            item1 = Items.COMMAND_BLOCK_MINECART;
                            break;
                        }
                        default: {
                            item1 = Items.MINECART;
                        }
                    }
                    itemstack = new ItemStack(item1);
                } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
                    itemstack = new ItemStack(((EntityBoat)this.objectMouseOver.entityHit).getItemBoat());
                } else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
                    itemstack = new ItemStack(Items.ARMOR_STAND);
                } else if (this.objectMouseOver.entityHit instanceof EntityEnderCrystal) {
                    itemstack = new ItemStack(Items.END_CRYSTAL);
                } else {
                    ResourceLocation resourcelocation = EntityList.func_191301_a(this.objectMouseOver.entityHit);
                    if (resourcelocation == null || !EntityList.ENTITY_EGGS.containsKey(resourcelocation)) {
                        return;
                    }
                    itemstack = new ItemStack(Items.SPAWN_EGG);
                    ItemMonsterPlacer.applyEntityIdToItemStack(itemstack, resourcelocation);
                }
            }
            if (itemstack.func_190926_b()) {
                String s2 = "";
                if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
                    s2 = Block.REGISTRY.getNameForObject(this.world.getBlockState(this.objectMouseOver.getBlockPos()).getBlock()).toString();
                } else if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
                    s2 = EntityList.func_191301_a(this.objectMouseOver.entityHit).toString();
                }
                LOGGER.warn("Picking on: [{}] {} gave null item", (Object)this.objectMouseOver.typeOfHit, (Object)s2);
            } else {
                InventoryPlayer inventoryplayer = this.player.inventory;
                if (tileentity != null) {
                    this.storeTEInStack(itemstack, tileentity);
                }
                int i2 = inventoryplayer.getSlotFor(itemstack);
                if (flag) {
                    inventoryplayer.setPickedItemStack(itemstack);
                    this.playerController.sendSlotPacket(this.player.getHeldItem(EnumHand.MAIN_HAND), 36 + inventoryplayer.currentItem);
                } else if (i2 != -1) {
                    if (InventoryPlayer.isHotbar(i2)) {
                        inventoryplayer.currentItem = i2;
                    } else {
                        this.playerController.pickItem(i2);
                    }
                }
            }
        }
    }

    private ItemStack storeTEInStack(ItemStack stack, TileEntity te2) {
        NBTTagCompound nbttagcompound = te2.writeToNBT(new NBTTagCompound());
        if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner")) {
            NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();
            nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
            stack.setTagCompound(nbttagcompound3);
            return stack;
        }
        stack.setTagInfo("BlockEntityTag", nbttagcompound);
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        nbttaglist.appendTag(new NBTTagString("(+NBT)"));
        nbttagcompound1.setTag("Lore", nbttaglist);
        stack.setTagInfo("display", nbttagcompound1);
        return stack;
    }

    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
        theCrash.getCategory().setDetail("Launched Version", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().setDetail("LWJGL", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().setDetail("OpenGL", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return String.valueOf(GlStateManager.glGetString(7937)) + " GL version " + GlStateManager.glGetString(7938) + ", " + GlStateManager.glGetString(7936);
            }
        });
        theCrash.getCategory().setDetail("GL Caps", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return OpenGlHelper.getLogText();
            }
        });
        theCrash.getCategory().setDetail("Using VBOs", new ICrashReportDetail<String>(){

            @Override
            public String call() {
                return Minecraft.this.gameSettings.useVbo ? "Yes" : "No";
            }
        });
        theCrash.getCategory().setDetail("Is Modded", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                String s2 = ClientBrandRetriever.getClientModName();
                if (!"vanilla".equals(s2)) {
                    return "Definitely; Client brand changed to '" + s2 + "'";
                }
                return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and client brand is untouched.";
            }
        });
        theCrash.getCategory().setDetail("Type", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().setDetail("Resource Packs", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                StringBuilder stringbuilder = new StringBuilder();
                for (String s2 : Minecraft.this.gameSettings.resourcePacks) {
                    if (stringbuilder.length() > 0) {
                        stringbuilder.append(", ");
                    }
                    stringbuilder.append(s2);
                    if (!Minecraft.this.gameSettings.incompatibleResourcePacks.contains(s2)) continue;
                    stringbuilder.append(" (incompatible)");
                }
                return stringbuilder.toString();
            }
        });
        theCrash.getCategory().setDetail("Current Language", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().setDetail("Profiler Position", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().setDetail("CPU", new ICrashReportDetail<String>(){

            @Override
            public String call() throws Exception {
                return OpenGlHelper.getCpu();
            }
        });
        if (this.world != null) {
            this.world.addWorldInfoToCrashReport(theCrash);
        }
        return theCrash;
    }

    public static Minecraft getMinecraft() {
        return theMinecraft;
    }

    public ListenableFuture<Object> scheduleResourcesRefresh() {
        return this.addScheduledTask(new Runnable(){

            @Override
            public void run() {
                Minecraft.this.refreshResources();
            }
        });
    }

    @Override
    public void addServerStatsToSnooper(Snooper playerSnooper) {
        playerSnooper.addClientStat("fps", debugFPS);
        playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
        playerSnooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("current_action", this.getCurrentAction());
        playerSnooper.addClientStat("language", this.gameSettings.language == null ? "en_us" : this.gameSettings.language);
        String s2 = ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        playerSnooper.addClientStat("endianness", s2);
        playerSnooper.addClientStat("subtitles", this.gameSettings.showSubtitles);
        playerSnooper.addClientStat("touch", this.gameSettings.touchscreen ? "touch" : "mouse");
        playerSnooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
        int i2 = 0;
        for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            playerSnooper.addClientStat("resource_pack[" + i2++ + "]", resourcepackrepository$entry.getResourcePackName());
        }
        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    private String getCurrentAction() {
        if (this.theIntegratedServer != null) {
            return this.theIntegratedServer.getPublic() ? "hosting_lan" : "singleplayer";
        }
        if (this.currentServerData != null) {
            return this.currentServerData.isOnLAN() ? "playing_lan" : "multiplayer";
        }
        return "out_of_game";
    }

    @Override
    public void addServerTypeToSnooper(Snooper playerSnooper) {
        playerSnooper.addStatToSnooper("opengl_version", GlStateManager.glGetString(7938));
        playerSnooper.addStatToSnooper("opengl_vendor", GlStateManager.glGetString(7936));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities contextcapabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", contextcapabilities.GL_ARB_arrays_of_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", contextcapabilities.GL_ARB_base_instance);
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", contextcapabilities.GL_ARB_blend_func_extended);
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", contextcapabilities.GL_ARB_clear_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", contextcapabilities.GL_ARB_color_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", contextcapabilities.GL_ARB_compatibility);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", contextcapabilities.GL_ARB_compressed_texture_pixel_storage);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", contextcapabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", contextcapabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", contextcapabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", contextcapabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", contextcapabilities.GL_ARB_depth_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", contextcapabilities.GL_ARB_depth_texture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", contextcapabilities.GL_ARB_draw_buffers);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", contextcapabilities.GL_ARB_draw_buffers_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", contextcapabilities.GL_ARB_draw_elements_base_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", contextcapabilities.GL_ARB_draw_indirect);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", contextcapabilities.GL_ARB_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", contextcapabilities.GL_ARB_explicit_attrib_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", contextcapabilities.GL_ARB_explicit_uniform_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", contextcapabilities.GL_ARB_fragment_layer_viewport);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", contextcapabilities.GL_ARB_fragment_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", contextcapabilities.GL_ARB_fragment_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", contextcapabilities.GL_ARB_fragment_program_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", contextcapabilities.GL_ARB_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", contextcapabilities.GL_ARB_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", contextcapabilities.GL_ARB_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", contextcapabilities.GL_ARB_gpu_shader5);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", contextcapabilities.GL_ARB_half_float_pixel);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", contextcapabilities.GL_ARB_half_float_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", contextcapabilities.GL_ARB_instanced_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", contextcapabilities.GL_ARB_map_buffer_alignment);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", contextcapabilities.GL_ARB_map_buffer_range);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", contextcapabilities.GL_ARB_multisample);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", contextcapabilities.GL_ARB_multitexture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", contextcapabilities.GL_ARB_occlusion_query2);
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", contextcapabilities.GL_ARB_pixel_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", contextcapabilities.GL_ARB_seamless_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", contextcapabilities.GL_ARB_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", contextcapabilities.GL_ARB_shader_stencil_export);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", contextcapabilities.GL_ARB_shader_texture_lod);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", contextcapabilities.GL_ARB_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", contextcapabilities.GL_ARB_shadow_ambient);
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", contextcapabilities.GL_ARB_stencil_texturing);
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", contextcapabilities.GL_ARB_sync);
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", contextcapabilities.GL_ARB_tessellation_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", contextcapabilities.GL_ARB_texture_border_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", contextcapabilities.GL_ARB_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", contextcapabilities.GL_ARB_texture_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", contextcapabilities.GL_ARB_texture_cube_map_array);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", contextcapabilities.GL_ARB_texture_non_power_of_two);
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", contextcapabilities.GL_ARB_uniform_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", contextcapabilities.GL_ARB_vertex_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", contextcapabilities.GL_ARB_vertex_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", contextcapabilities.GL_ARB_vertex_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", contextcapabilities.GL_ARB_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", contextcapabilities.GL_EXT_bindable_uniform);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", contextcapabilities.GL_EXT_blend_equation_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", contextcapabilities.GL_EXT_blend_func_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", contextcapabilities.GL_EXT_blend_minmax);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", contextcapabilities.GL_EXT_blend_subtract);
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", contextcapabilities.GL_EXT_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", contextcapabilities.GL_EXT_framebuffer_multisample);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", contextcapabilities.GL_EXT_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", contextcapabilities.GL_EXT_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", contextcapabilities.GL_EXT_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", contextcapabilities.GL_EXT_gpu_program_parameters);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", contextcapabilities.GL_EXT_gpu_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", contextcapabilities.GL_EXT_multi_draw_arrays);
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", contextcapabilities.GL_EXT_packed_depth_stencil);
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", contextcapabilities.GL_EXT_paletted_texture);
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", contextcapabilities.GL_EXT_rescale_normal);
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", contextcapabilities.GL_EXT_separate_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", contextcapabilities.GL_EXT_shader_image_load_store);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", contextcapabilities.GL_EXT_shadow_funcs);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", contextcapabilities.GL_EXT_shared_texture_palette);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", contextcapabilities.GL_EXT_stencil_clear_tag);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", contextcapabilities.GL_EXT_stencil_two_side);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", contextcapabilities.GL_EXT_stencil_wrap);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", contextcapabilities.GL_EXT_texture_3d);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", contextcapabilities.GL_EXT_texture_array);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", contextcapabilities.GL_EXT_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", contextcapabilities.GL_EXT_texture_integer);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", contextcapabilities.GL_EXT_texture_lod_bias);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", contextcapabilities.GL_EXT_texture_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", contextcapabilities.GL_EXT_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", contextcapabilities.GL_EXT_vertex_weighting);
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GlStateManager.glGetInteger(35658));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GlStateManager.glGetInteger(35657));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GlStateManager.glGetInteger(34921));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GlStateManager.glGetInteger(35660));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GlStateManager.glGetInteger(34930));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_array_texture_layers]", GlStateManager.glGetInteger(35071));
        GlStateManager.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", Minecraft.getGLMaximumTextureSize());
        GameProfile gameprofile = this.session.getProfile();
        if (gameprofile != null && gameprofile.getId() != null) {
            playerSnooper.addStatToSnooper("uuid", Hashing.sha1().hashBytes(gameprofile.getId().toString().getBytes(Charsets.ISO_8859_1)).toString());
        }
    }

    public static int getGLMaximumTextureSize() {
        for (int i2 = 16384; i2 > 0; i2 >>= 1) {
            GlStateManager.glTexImage2D(32868, 0, 6408, i2, i2, 0, 6408, 5121, null);
            int j2 = GlStateManager.glGetTexLevelParameteri(32868, 0, 4096);
            if (j2 == 0) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public boolean isSnooperEnabled() {
        return this.gameSettings.snooperEnabled;
    }

    public void setServerData(ServerData serverDataIn) {
        this.currentServerData = serverDataIn;
    }

    @Nullable
    public ServerData getCurrentServerData() {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerIsRunning;
    }

    public boolean isSingleplayer() {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    @Nullable
    public IntegratedServer getIntegratedServer() {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer() {
        IntegratedServer integratedserver;
        if (theMinecraft != null && (integratedserver = theMinecraft.getIntegratedServer()) != null) {
            integratedserver.stopServer();
        }
    }

    public Snooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }

    public static long getSystemTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    public boolean isFullScreen() {
        return this.fullscreen;
    }

    public Session getSession() {
        return this.session;
    }

    public PropertyMap getProfileProperties() {
        if (this.profileProperties.isEmpty()) {
            GameProfile gameprofile = this.getSessionService().fillProfileProperties(this.session.getProfile(), false);
            this.profileProperties.putAll(gameprofile.getProperties());
        }
        return this.profileProperties;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager() {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository() {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager() {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks() {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit() {
        return this.jvm64bit;
    }

    public boolean isGamePaused() {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler() {
        return this.mcSoundHandler;
    }

    public MusicTicker.MusicType getAmbientMusicType() {
        if (this.currentScreen instanceof GuiWinGame) {
            return MusicTicker.MusicType.CREDITS;
        }
        if (this.player != null) {
            if (this.player.world.provider instanceof WorldProviderHell) {
                return MusicTicker.MusicType.NETHER;
            }
            if (this.player.world.provider instanceof WorldProviderEnd) {
                return this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? MusicTicker.MusicType.END_BOSS : MusicTicker.MusicType.END;
            }
            return this.player.capabilities.isCreativeMode && this.player.capabilities.allowFlying ? MusicTicker.MusicType.CREATIVE : MusicTicker.MusicType.GAME;
        }
        return MusicTicker.MusicType.MENU;
    }

    public void dispatchKeypresses() {
        int i2;
        int n2 = i2 = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        if (!(i2 == 0 || Keyboard.isRepeatEvent() || this.currentScreen instanceof GuiControls && ((GuiControls)this.currentScreen).time > Minecraft.getSystemTime() - 20L || !Keyboard.getEventKeyState())) {
            if (!(this.currentScreen instanceof GuiScreen)) {
                ExtremeHack.onKeyPress(i2);
            }
            if (i2 == this.gameSettings.keyBindFullscreen.getKeyCode()) {
                this.toggleFullscreen();
            } else if (i2 == this.gameSettings.keyBindScreenshot.getKeyCode()) {
                this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc));
            } else if (i2 == 48 && GuiScreen.isCtrlKeyDown() && (this.currentScreen == null || this.currentScreen != null && !this.currentScreen.func_193976_p())) {
                this.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);
                if (this.currentScreen instanceof ScreenChatOptions) {
                    ((ScreenChatOptions)this.currentScreen).func_193024_a();
                }
            }
        }
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    @Nullable
    public Entity getRenderViewEntity() {
        return this.renderViewEntity;
    }

    public void setRenderViewEntity(Entity viewingEntity) {
        this.renderViewEntity = viewingEntity;
        this.entityRenderer.loadEntityShader(viewingEntity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <V> ListenableFuture<V> addScheduledTask(Callable<V> callableToSchedule) {
        Validate.notNull(callableToSchedule);
        if (this.isCallingFromMinecraftThread()) {
            try {
                return Futures.immediateFuture(callableToSchedule.call());
            }
            catch (Exception exception) {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
        ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callableToSchedule);
        Queue<FutureTask<?>> queue = this.scheduledTasks;
        synchronized (queue) {
            this.scheduledTasks.add(listenablefuturetask);
            return listenablefuturetask;
        }
    }

    @Override
    public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.addScheduledTask(Executors.callable(runnableToSchedule));
    }

    @Override
    public boolean isCallingFromMinecraftThread() {
        return Thread.currentThread() == this.mcThread;
    }

    public BlockRendererDispatcher getBlockRendererDispatcher() {
        return this.blockRenderDispatcher;
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public RenderItem getRenderItem() {
        return this.renderItem;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public <T> ISearchTree<T> func_193987_a(SearchTreeManager.Key<T> p_193987_1_) {
        return this.field_193995_ae.func_194010_a(p_193987_1_);
    }

    public static int getDebugFPS() {
        return debugFPS;
    }

    public FrameTimer getFrameTimer() {
        return this.frameTimer;
    }

    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean isConnected) {
        this.connectedToRealms = isConnected;
    }

    public DataFixer getDataFixer() {
        return this.dataFixer;
    }

    public float getRenderPartialTicks() {
        return this.timer.field_194147_b;
    }

    public float func_193989_ak() {
        return this.timer.field_194148_c;
    }

    public BlockColors getBlockColors() {
        return this.blockColors;
    }

    public boolean isReducedDebug() {
        return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
    }

    public GuiToast func_193033_an() {
        return this.field_193034_aS;
    }

    public Tutorial func_193032_ao() {
        return this.field_193035_aW;
    }
}

