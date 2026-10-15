const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const STUDENT_UID = "student_siti_123";
const OTHER_STUDENT_UID = "student_budi_456";
const TEACHER_UID = "teacher_ahzami_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read app_config or student_submissions", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("app_config").doc("admin_policy").get());
  await assertFails(unauthDb.collection("student_submissions").get());
});

test("Authenticated student: can read dynamic contents and bibliographies", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("dynamic_contents").doc("guide_1").set({
      id: "guide_1",
      category: "GUIDE",
      subCategory: "PANDUAN_SISWA_GURU",
      title: "Panduan Aplikasi",
      content: "Isi panduan etnosains Parijoto",
    });
    await context.firestore().collection("bibliographies").doc("ref_1").set({
      id: "ref_1",
      title: "Antosianin Parijoto",
      authors: "Niswah et al.",
      year: "2023",
      source: "Jurnal Riset Kimia",
      category: "JURNAL",
    });
  });

  const studentDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  await assertSucceeds(studentDb.collection("dynamic_contents").doc("guide_1").get());
  await assertSucceeds(studentDb.collection("bibliographies").doc("ref_1").get());
});

test("Student: can submit own E-LKPD submission", async () => {
  const studentDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  await assertSucceeds(
    studentDb.collection("student_submissions").doc("sub_1").set({
      id: "sub_1",
      studentId: STUDENT_UID,
      studentName: "Siti Rahma",
      submissionType: "ACTIVITY_1",
      title: "E-LKPD Aktivitas 1",
      payloadJson: "{}",
    })
  );
});

test("Student: cannot read another student's submission", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("student_submissions").doc("sub_budi").set({
      id: "sub_budi",
      studentId: OTHER_STUDENT_UID,
      studentName: "Budi Santoso",
      submissionType: "ACTIVITY_1",
      title: "E-LKPD Aktivitas 1",
      payloadJson: "{}",
    });
  });

  const studentDb = testEnv.authenticatedContext(STUDENT_UID).firestore();
  await assertFails(studentDb.collection("student_submissions").doc("sub_budi").get());
});

test("Teacher: can read all student submissions", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(TEACHER_UID).set({
      id: TEACHER_UID,
      nama: "Ahzami, S.Pd., Gr",
      role: "TEACHER",
    });
    await context.firestore().collection("student_submissions").doc("sub_siti").set({
      id: "sub_siti",
      studentId: STUDENT_UID,
      studentName: "Siti Rahma",
      submissionType: "ACTIVITY_1",
      title: "E-LKPD Aktivitas 1",
      payloadJson: "{}",
    });
  });

  const teacherDb = testEnv.authenticatedContext(TEACHER_UID, { email: "ahzami@guru.sma.belajar.id" }).firestore();
  await assertSucceeds(teacherDb.collection("student_submissions").doc("sub_siti").get());
});
