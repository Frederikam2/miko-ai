package strucures

import memory.*
import roles.*
import screeps.api.*
import screeps.api.structures.StructureSpawn
import screeps.utils.unsafe.jsObject
import util.TickData

object Spawn {
    fun run(room: Room) {
        val creeps = TickData.creepsByHome[room.name] ?: emptyList()
        val spawn = room.find(FIND_MY_SPAWNS).firstOrNull() { it.spawning == null }
        spawn ?: return

        val harvesters = creeps.count { it.memory.role == Harvester }
        val haulers = creeps.count { it.memory.role == Hauler }
        val upgraders = creeps.count { it.memory.role == Upgrader }
        val builders = creeps.count { it.memory.role == Builder }

        // count of sources in room
        val sourcesSize = room.find(FIND_SOURCES).size

        room.memory.noHarvesters = harvesters == 0
        room.memory.primitiveHarvesters = builders == 0 || haulers < sourcesSize

        // primitive room spawning, X = number of sources in room
        // Priority: X Harvesters, 1 Upgrader, X Builders, X Haulers
        if (harvesters == 0 || harvesters < sourcesSize) {
            if (spawn.handleSpawn(Harvester, room.energyCapacityAvailable)) return
        } else if (upgraders == 0) {
            if (spawn.handleSpawn(Upgrader, room.energyCapacityAvailable)) return
        } else if (builders == 0) {
            if (spawn.handleSpawn(Builder, room.energyCapacityAvailable)) return
        } else if (haulers < sourcesSize) {
            if (spawn.handleSpawn(Hauler, room.energyCapacityAvailable)) return
        }

        // non-primitive spawning
        if (!room.memory.primitiveHarvesters) {
            if (upgraders < 2) {
                if (spawn.handleSpawn(Upgrader, room.energyCapacityAvailable)) return
            } else if (haulers < sourcesSize) {
                if (spawn.handleSpawn(Hauler, room.energyCapacityAvailable)) return
            } else if (builders < sourcesSize) {
                if (spawn.handleSpawn(Builder, room.energyCapacityAvailable)) return
            }
        }
    }

    /**
     * @return if the spawn was successful
     */
    private fun StructureSpawn.handleSpawn(role: IRole, budget: Int): Boolean {
        val parts = role.getSpawnParts(budget) ?: return false
        val roomName = room.name
        val creepMemory = jsObject<CreepMemory> {
            this.role = role
            this.role = role
            this.room = roomName
        }
        val status = spawnCreep(parts, generateName(), jsObject {
            memory = creepMemory
        })

        if (status == OK) role.onSpawning(room, creepMemory)

        return status == OK
    }

    private val firstNames = arrayOf("Ahmya", "Ai", "Aia", "Aika", "Aiko", "Aimi", "Aina", "Ainu", "Airi", "Aiya", "Akahana", "Akane", "Akari", "Aki", "Akiara", "Akiko", "Akira", "Akito", "Amaterasu", "Amaya", "Ami", "Aneko", "Anzu", "Aratani", "Arisu", "Asa", "Asami", "Asuga", "Asuka", "Atsuko", "Au", "Aya", "Aya", "Ayaka", "Ayaka", "Ayako", "Ayame", "Ayano", "Ayumi", "Azumi", "Bashira", "Botan", "Chiaki", "Chiasa", "Chie", "Chieko", "Chiharu", "Chihiro", "Chika", "Chika", "Chinami", "Chinatsu", "Chiyo", "Chiyoko", "Chizue", "Cho", "Chuya", "Dai", "Danuja", "Den", "Doi", "Eiko", "Emi", "Emica", "Emiko", "Ena", "Eri", "Eshima", "Etsu", "Etsudo", "Etsuko", "Fuji", "Fumiko", "Fuyuko", "Gen", "Gina", "Hachi", "Haia", "Hana", "Hanae", "Hanako", "Hanami", "Haru", "Harue", "Haruhi", "Haruka", "Haruki", "Haruko", "Harumi", "Haruna", "Hatsu", "Hatsuko", "Hatsumomo", "Haya", "Hayami", "Hekima", "Hibiki", "Hide", "Hideko", "Hikari", "Hikaru", "Hina", "Hinata", "Hiriko", "Hiro", "Hiroko", "Hiromi", "Hisa", "Hisako", "Hitomi", "Homura", "Honoka", "Hoshi", "Hoshiko", "Hotaru", "Humiya", "Iku", "Ima", "Isamu", "Ito", "Iwa", "Izanami", "Izumi", "Japana", "Jin", "Jona", "Jun", "Junko", "Kaede", "Kagami", "Kagome", "Kaida", "Kairi", "Kairy", "Kaiya", "Kaiyo", "Kame", "Kamiko", "Kana", "Kano", "Kanon", "Kaori", "Kasumi", "Kata", "Katana", "Katsu", "Katsuki", "Katsumi", "Kayda", "Kayo", "Kazane", "Kazashi", "Kazue", "Kei", "Keiko", "Keina", "Keomi", "Kiaria", "Kichi", "Kiko", "Kiku", "Kimi", "Kimi", "Kimika", "Kimiko", "Kimmi", "Kin", "Kioka", "Kioko", "Kirika", "Kishi", "Kita", "Kitiara", "Kiwa", "Kiyoko", "Kiyomi", "Ko", "Koemi", "Koge", "Kohaku", "Kohana", "Koko", "Kokoro", "Kosuke", "Koto", "Kotone", "Kozakura", "Kozue", "Kukiko", "Kuma", "Kumi", "Kumiko", "Kura", "Kuri", "Kyo", "Kyoko", "Machiko", "Maeko", "Maemi", "Maho", "Mai", "Maiko", "Maiya", "Makaira", "Maki", "Makiko", "Makoto", "Mami", "Mamiko", "Mana", "Manami", "Mao", "Mari", "Mariko", "Marri", "Masae", "Masako", "Masami", "Masumi", "Masuyo", "Matsuko", "Mayu", "Mayuko", "Mayumi", "Megumi", "Mei", "Mi", "Michi", "Michiko", "Midori", "Mieko", "Miho", "Mihoko", "Mika", "Mikan", "Mikasa", "Miki", "Mikka", "Miku", "Minako", "Minami", "Minato", "Mine", "Minoru", "Mio", "Mirai", "Misa", "Misaki", "Misako", "Mitsu", "Mitsuko", "Miu", "Miwa", "Miya", "Miyah", "Miyako", "Miyo", "Miyoko", "Miyu", "Miyuki", "Mizuki", "Moe", "Momiji", "Momo", "Momoe", "Momoka", "Momoko", "Mon", "Morie", "Morika", "Moriko", "Morina", "Muika", "Mura", "Nagi", "Nakano", "Namie", "Namika", "Nana", "Nanako", "Nanami", "Nao", "Naoko", "Nara", "Nariko", "Narumi", "Natsuki", "Natsuko", "Natsumi", "Ne", "Nisbett", "Nishi", "Noa", "Nobuko", "Nori", "Noriko", "Norita", "Nozomi", "Nyoko", "Ohta", "Okemia", "Oki", "Okimi", "Orino", "Rai", "Raku", "Ran", "Ran", "Rei", "Reiki", "Reiko", "Reina", "Ren", "Rie", "Rieko", "Rika", "Riko", "Rikona", "Rin", "Rini", "Rio", "Risa", "Rui", "Rumi", "Ruqa", "Ruri", "Ryoko", "Sachi", "Sachiko", "Sada", "Sadako", "Sadashi", "Saeko", "Sai", "Saiua", "Sajonara", "Sakae", "Sakai", "Saki", "Sakiko", "Saku", "Sakura", "Sakurako", "Sango", "Sanyu", "Saori", "Saory", "Satchiko", "Satoko", "Satomi", "Satsuki", "Sawako", "Sayaka", "Sayua", "Sayuri", "Seijun", "Seika", "Seiko", "Seina", "Seiren", "Sen", "Setsuko", "Shaiwase", "Shigeko", "Shika", "Shiki", "Shiko", "Shiniqua", "Shinju", "Shino", "Shinobu", "Shion", "Shiori", "Shizu", "Shizuka", "Shoko", "Shun", "Shun", "Shynah", "Someina", "Sora", "Sorano", "Subaru", "Sueko", "Sugi", "Suki", "Sumiko", "Sumire", "Sumiye", "Sunako", "Susumu", "Suzu", "Suzue", "Suzume", "Taka", "Takako", "Takara", "Take", "Taki", "Tamaki", "Tamami", "Tamane", "Tamashini", "Tami", "Tamiko", "Tamura", "Taney", "Tani", "Taree", "Taru", "Tatsu", "Tatsuo", "Taura", "Teruko", "Toki", "Tokiwa", "Tomiju", "Tomiko", "Tomo", "Tomoe", "Tomoko", "Tomomi", "Tonica", "Tora", "Toru", "Toshi", "Toshiko", "Tsubaki", "Tsubame", "Tsubasa", "Tsukiko", "Tsunade", "Tsuru", "Ume", "Umi", "Usagi", "Uta", "Utano", "Wakana", "Wakumi", "Yasu", "Yasuko", "Yo", "Yoko", "Yori", "Yoru", "Yoshi", "Yoshiko", "Yoshima", "Yoshino", "Youko", "Yua", "Yui", "Yuina", "Yuka", "Yuki", "Yukiko", "Yuko", "Yume", "Yumi", "Yuna", "Yuri", "Yuriko")
    private val surnames = arrayOf("Sato", "Suzuki", "Takahashi", "Tanaka", "Watanabe", "Ito", "Yamamoto", "Nakamura", "Kobayashi", "Kato", "Yoshida", "Yamada", "Sasaki", "Yamaguchi", "Saito", "Matsumoto", "Inoue", "Kimura", "Hayashi", "Shimizu", "Yamazaki", "Mori", "Abe", "Ikeda", "Hashimoto", "Yamashita", "Ishikawa", "Nakajima", "Maeda", "Fujita", "Ogawa", "Goto", "Okada", "Hasegawa", "Murakami", "Kondo", "Ishii", "Sakamoto", "Endo", "Aoki", "Fujii", "Nishimura", "Fukuda", "Ota", "Miura", "Fujiwara", "Okamoto", "Matsuda", "Nakagawa", "Nakano", "Harada", "Ono", "Tamura", "Takeuchi", "Kaneko", "Wada", "Nakayama", "Ishida", "Ueda", "Morita", "Hara", "Shibata", "Sakai", "Kudo", "Yokoyama", "Miyazaki", "Miyamoto", "Uchida", "Takagi", "Ando", "Taniguchi", "Ohno", "Maruyama", "Imai", "Takada", "Fujimoto", "Takeda", "Murata", "Ueno", "Sugiyama", "Masuda", "Sugawara", "Hirano", "Kojima", "Otsuka", "Chiba", "Kubo", "Matsui", "Iwasaki", "Sakurai", "Kinoshita", "Noguchi", "Matsuo", "Nomura", "Kikuchi", "Sano", "Onishi", "Sugimoto", "Arai")
    private fun generateName() = firstNames.random() + "-" + surnames.random()
}
