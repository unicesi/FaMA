/**
 * 	This file is part of FaMaTS.
 *
 *     FaMaTS is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FaMaTS is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with FaMaTS.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.util.Node;

public class FaMaPlainTextParser {
	
	public FAMAFeatureModel parseModel(String path) {

		FAMAFeatureModel res = null;

		try {
			FileInputStream in = new FileInputStream(path);
			Analex an = new Analex(in);
			Anasint as = new Anasint(an);
			res = parseModel(as);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;

	}

	public FAMAFeatureModel parseModelFromString(String s) {
		FAMAFeatureModel res = null;
		StringReader sReader = new StringReader(s);
		Analex an = new Analex(sReader);
		Anasint as = new Anasint(an);
		res = parseModel(as);
		return res;
	}

	private FAMAFeatureModel parseModel(Anasint as) {
		FAMAFeatureModel res = null;
		try {
//			as.setASTNodeClass("es.us.isa.FAMA.parser.MyAST");
			Collection<String> errors;
			errors = as.entrada();
			AST tree = as.getAST();
//			ASTFrame frame = new ASTFrame("AST", tree);
//			frame.setVisible(true);
			if (!errors.isEmpty()) {
				System.out
						.println("Warning, errors detected on Syntactic Analysis");
				showErrors(errors);
			} else {
				FaMaTreeParser sem = new FaMaTreeParser();
//				sem.setASTNodeClass("es.us.isa.FAMA.parser.MyAST");
				TreeParserResult result = sem.entrada(tree);
				errors = result.getErrors();
				if (!errors.isEmpty()){
					System.out
						.println("Warning, errors detected on Semantic Analysis");
					showErrors(errors);
				}
				else{
					res = result.getFM();
				}
			}
		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (TokenStreamException e) {
			e.printStackTrace();
		}
		return res;
	}

	public AST parseModelAST(String path) {

		AST res = null;
		try {
			FileInputStream in = new FileInputStream(path);
			Analex an = new Analex(in);
			Anasint as = new Anasint(an);
//			as.setASTNodeClass("es.us.isa.FAMA.parser.MyAST");
			Collection<String> errors = as.entrada();
			if (!errors.isEmpty()) {
				showErrors(errors);
			}
			res = as.getAST();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (RecognitionException e) {
			e.printStackTrace();
		} catch (TokenStreamException e) {
			e.printStackTrace();
		}
		return res;

	}

//	public static void main(String[] args) {
//
//		FMFParser parser = new FMFParser();
//		FAMAFeatureModel fm = parser.parseModel(args[0]);
//		System.out.println(fm);
//		// AttributedFeature f = fm.searchFeatureByName("Wifi");
//		// GenericAttribute att = f.searchAttributeByName("att3");
//		// System.out.println(att);
//
//	}

	private void showErrors(Collection<String> errors) {

		Iterator<String> it = errors.iterator();
		while (it.hasNext()) {
			String e = it.next();
			System.out.println(e);
		}

	}

	private void walkTree(AST ast, Node<String> node) {

		node.setData(ast.getText());
		int children = ast.getNumberOfChildren();
		if (children > 0) {
			AST child = ast.getFirstChild();
			Node<String> n = new Node<String>();
			node.addChild(n);
			walkTree(child, n);
			for (int i = 1; i < children; i++) {
				AST aux = child.getNextSibling();
				n = new Node<String>();
				node.addChild(n);
				walkTree(aux, n);
			}
		}

	}
}
